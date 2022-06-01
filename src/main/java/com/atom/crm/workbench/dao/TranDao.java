package com.atom.crm.workbench.dao;

import com.atom.crm.workbench.domain.Tran;

import java.util.List;

public interface TranDao {

    int save(Tran t);

    List<Tran> getTransactionList();
}
