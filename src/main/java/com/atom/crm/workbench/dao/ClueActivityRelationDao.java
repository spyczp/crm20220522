package com.atom.crm.workbench.dao;

import com.atom.crm.workbench.domain.Activity;
import com.atom.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    List<Activity> GetActivityListByClueId(String clueId);

    int deleteById(String id);

    int bound(ClueActivityRelation car);

    int bound2(List<ClueActivityRelation> carList);
}
