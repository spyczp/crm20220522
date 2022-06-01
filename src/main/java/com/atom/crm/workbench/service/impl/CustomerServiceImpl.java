package com.atom.crm.workbench.service.impl;

import com.atom.crm.utils.SqlSessionUtil;
import com.atom.crm.workbench.dao.CustomerDao;
import com.atom.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<String> getCustomerName(String name) {

        List<String> customerNameList =  customerDao.getCustomerNameByName(name);

        return customerNameList;
    }
}
