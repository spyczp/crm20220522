package com.atom.crm.workbench.dao;

import com.atom.crm.workbench.domain.Customer;

import java.util.List;

public interface CustomerDao {

    Customer getByName(String customerName);

    int save(Customer customer);

    List<String> getCustomerNameByName(String name);

}
