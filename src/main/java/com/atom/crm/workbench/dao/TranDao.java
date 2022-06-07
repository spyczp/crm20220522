package com.atom.crm.workbench.dao;

import com.atom.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran t);

    List<Tran> getTransactionList(Map<String, Object> map);

    int getCountByCondition(Map<String, Object> map);

    Tran getById(String id);

    int changeStage(Tran t);

    int getCount();

    List<Map<String, Object>> getCountGroupByStage();
}
