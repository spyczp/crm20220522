package com.atom.crm.workbench.service.impl;

import com.atom.crm.settings.dao.UserDao;
import com.atom.crm.settings.domain.User;
import com.atom.crm.utils.DateTimeUtil;
import com.atom.crm.utils.SqlSessionUtil;
import com.atom.crm.utils.UUIDUtil;
import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.dao.*;
import com.atom.crm.workbench.domain.*;
import com.atom.crm.workbench.service.ClueService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {
    //线索相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);

    //客户相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    //联系人相关表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    //交易相关表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);




    @Override
    public List<User> getUserList() {

        List<User> userList = userDao.getUserList();

        return userList;
    }

    @Override
    public PaginationVO<Clue> getByCondition(Map<String, Object> map) {

        int count = clueDao.getCountByCondition(map);

        List<Clue> clueList = clueDao.getByCondition(map);

        PaginationVO<Clue> paginationVO = new PaginationVO<>();

        paginationVO.setTotal(count);
        paginationVO.setDataList(clueList);

        return paginationVO;
    }

    @Override
    public boolean save(Clue clue) {

        boolean flag = true;

        int count = clueDao.save(clue);

        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Clue getById(String id) {

        Clue clue = clueDao.getById(id);

        return clue;
    }

    @Override
    public List<Activity> GetActivityListByClueId(String clueId) {

        List<Activity> activityList = clueActivityRelationDao.GetActivityListByClueId(clueId);

        return activityList;
    }

    @Override
    public boolean unbound(String id) {

        boolean flag = true;

        int count = clueActivityRelationDao.deleteById(id);

        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean bound(List<ClueActivityRelation> carList) {

        boolean flag = true;
        /*int count = 0;




        for(ClueActivityRelation car: carList){
            clueActivityRelationDao.bound(car);
            count++;
        }*/
        int count = clueActivityRelationDao.bound2(carList);


        if(count != carList.size()){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {

        String createTime = DateTimeUtil.getSysTime();

        boolean flag = true;

        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue clue = clueDao.getById(clueId);

        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = clue.getCompany();
        Customer customer =  customerDao.getByName(company);

        //若客户不存在，则新建
        if(customer == null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(clue.getAddress());
            customer.setOwner(clue.getOwner());
            customer.setName(company);
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setContactSummary(clue.getContactSummary());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setDescription(clue.getDescription());

            int count = customerDao.save(customer);
            if(count != 1){
                flag = false;
            }
        }

        //(3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setOwner(clue.getOwner());
        contacts.setSource(clue.getSource());
        contacts.setCustomerId(customer.getId());
        contacts.setFullname(clue.getFullname());
        contacts.setAppellation(clue.getAppellation());
        contacts.setEmail(clue.getEmail());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setCreateBy(createBy);
        contacts.setCreateTime(createTime);
        contacts.setDescription(clue.getDescription());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setAddress(clue.getAddress());

        int count1 = contactsDao.save(contacts);

        if(count1 != 1){
            flag = false;
        }

        //(4) 线索备注转换到客户备注以及联系人备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getByClueId(clueId);

        for(ClueRemark clueRemark: clueRemarkList){

            String noteContent = clueRemark.getNoteContent();

            //每条线索备注对应创建一条客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(customer.getId());
            int count2 = customerRemarkDao.save(customerRemark);
            if(count2 != 1){
                flag = false;
            }

            //每条线索备注对应创建一条联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setEditFlag("0");
            contactsRemark.setContactsId(contacts.getId());
            int count3 = contactsRemarkDao.save(contactsRemark);
            if(count3 != 1){
                flag = false;
            }
        }

        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getByClueId(clueId);

        for(ClueActivityRelation clueActivityRelation: clueActivityRelationList){

            String activityId = clueActivityRelation.getActivityId();

            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setActivityId(activityId);

            int count4 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count4 != 1){
                flag = false;
            }
        }

        //(6) 如果有创建交易需求，创建一条交易
        if(t != null){

            t.setOwner(clue.getOwner());
            t.setCustomerId(customer.getId());
            t.setSource(clue.getSource());
            t.setContactsId(contacts.getId());
            t.setDescription(clue.getDescription());
            t.setContactSummary(clue.getContactSummary());
            t.setNextContactTime(clue.getNextContactTime());

            int count5 = tranDao.save(t);
            if(count5 != 1){
                flag = false;
            }
        }


        return flag;
    }


}
