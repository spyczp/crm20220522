package com.atom.crm.workbench.dao;

import com.atom.crm.workbench.domain.Customer;

public interface CustomerDao {

    Customer getByName(String company);

    int save(Customer customer);
}
