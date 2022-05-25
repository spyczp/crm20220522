package com.atom.crm.workbench.service;

import com.atom.crm.settings.domain.User;
import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueService {
    List<User> getUserList();

    PaginationVO<Clue> getByCondition(Map<String, Object> map);
}
