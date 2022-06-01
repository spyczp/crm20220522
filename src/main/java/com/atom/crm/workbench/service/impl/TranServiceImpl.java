package com.atom.crm.workbench.service.impl;

import com.atom.crm.utils.SqlSessionUtil;
import com.atom.crm.utils.UUIDUtil;
import com.atom.crm.workbench.dao.CustomerDao;
import com.atom.crm.workbench.dao.TranDao;
import com.atom.crm.workbench.dao.TranHistoryDao;
import com.atom.crm.workbench.domain.Customer;
import com.atom.crm.workbench.domain.Tran;
import com.atom.crm.workbench.domain.TranHistory;
import com.atom.crm.workbench.service.TranService;

import java.util.List;

public class TranServiceImpl implements TranService {

    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);

    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);


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
    public List<Tran> getTransactionList() {

        List<Tran> tranList = tranDao.getTransactionList();

        return tranList;
    }
}
