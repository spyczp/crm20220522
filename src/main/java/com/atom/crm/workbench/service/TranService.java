package com.atom.crm.workbench.service;

import com.atom.crm.workbench.domain.Tran;

import java.util.List;

public interface TranService {
    boolean save(Tran tran, String customerName);

    List<Tran> getTransactionList();
}
