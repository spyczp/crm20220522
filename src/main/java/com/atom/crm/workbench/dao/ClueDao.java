package com.atom.crm.workbench.dao;

import com.atom.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int getCountByCondition(Map<String, Object> map);

    List<Clue> getByCondition(Map<String, Object> map);
}
