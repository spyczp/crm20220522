package com.atom.crm.workbench.service;

import com.atom.crm.settings.domain.User;
import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.domain.Activity;
import com.atom.crm.workbench.domain.Clue;
import com.atom.crm.workbench.domain.ClueActivityRelation;

import java.util.List;
import java.util.Map;

public interface ClueService {
    List<User> getUserList();

    PaginationVO<Clue> getByCondition(Map<String, Object> map);

    boolean save(Clue clue);

    Clue getById(String id);

    List<Activity> GetActivityListByClueId(String clueId);

    boolean unbound(String id);

    boolean bound(List<ClueActivityRelation> carList);
}
