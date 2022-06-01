package com.atom.crm.workbench.service;

import com.atom.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranService {
    boolean save(Tran tran, String customerName);

    List<Tran> getTransactionList(Map<String, Object> map);
}
