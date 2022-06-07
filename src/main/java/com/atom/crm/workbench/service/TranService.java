package com.atom.crm.workbench.service;

import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.domain.Tran;
import com.atom.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    boolean save(Tran tran, String customerName);

    PaginationVO<Tran> getTransactionList(Map<String, Object> map);

    Tran getById(String id);

    List<TranHistory> getTranHistoryByTranId(String tranId);

    boolean changeStage(Tran t);

    Map<String, Object> getChart();
}
