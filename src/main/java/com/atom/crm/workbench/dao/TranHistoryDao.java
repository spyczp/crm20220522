package com.atom.crm.workbench.dao;

import com.atom.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryDao {

    int save(TranHistory tranHistory);

    List<TranHistory> getByTranId(String tranId);
}
