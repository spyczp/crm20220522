package com.atom.crm.workbench.service.impl;

import com.atom.crm.settings.dao.DicValueDao;
import com.atom.crm.utils.SqlSessionUtil;
import com.atom.crm.utils.UUIDUtil;
import com.atom.crm.workbench.dao.ContactsDao;
import com.atom.crm.workbench.dao.CustomerDao;
import com.atom.crm.workbench.dao.TranDao;
import com.atom.crm.workbench.dao.TranHistoryDao;
import com.atom.crm.workbench.domain.Customer;
import com.atom.crm.workbench.domain.Tran;
import com.atom.crm.workbench.domain.TranHistory;
import com.atom.crm.workbench.service.TranService;

import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);

    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);

    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);


    @Override
    public boolean save(Tran tran, String customerName) {

        boolean flag = true;

        /*
        * 浏览器给了顾客名称，我们需要把顾客id保存到数据库。
        * 所以，首先得根据顾客名称找到顾客id。
        * 如果顾客id不存在，我们需要新建一个顾客。
        *
        * */
        Customer customer = customerDao.getByName(customerName);
        if(customer == null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setOwner(tran.getOwner());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(tran.getCreateTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setDescription(tran.getDescription());

            int count1 = customerDao.save(customer);
            if(count1 != 1){
                flag = false;
            }
        }

        tran.setCustomerId(customer.getId());

        //保存交易
        int count2 = tranDao.save(tran);
        if(count2 != 1){
            flag = false;
        }

        //创建并保存交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setStage(tran.getStage());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setCreateTime(tran.getCreateTime());
        tranHistory.setTranId(tran.getId());

        int count3 = tranHistoryDao.save(tranHistory);
        if(count3 != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public List<Tran> getTransactionList(Map<String, Object> map) {
        /*
        * 因为在浏览器页面，客户名称和联系人名称在tran表中不存在，表中只有对应的id。
        * 所以，我们得从对应的表中找数据。若客户名称和联系人名称其中一个信息在对应表中
        * 找不到，则应该返回空给浏览器。
        *
        * 把tran表和customer表以及contacts表连接起来查询就可以了
        *
        * 20220601 22:48 写到这里，明天再写。
        * */

        List<Tran> tranList = tranDao.getTransactionList(map);

        //在创建交易的时候，获取的stage，type，source是id
        //把交易数据返回给浏览器时，我们需要把stage，type和source转换成文字
        //所以，得访问dic_value数据表，获取stage，type和source对应的value
        for(Tran t: tranList){

            String stage = dicValueDao.getValueById(t.getStage());
            String type = dicValueDao.getValueById(t.getType());
            String source = dicValueDao.getValueById(t.getSource());

            t.setStage(stage);
            t.setType(type);
            t.setSource(source);

        }

        return tranList;
    }
}
