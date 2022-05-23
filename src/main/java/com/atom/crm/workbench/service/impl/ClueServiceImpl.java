package com.atom.crm.workbench.service.impl;

import com.atom.crm.settings.dao.UserDao;
import com.atom.crm.settings.domain.User;
import com.atom.crm.utils.SqlSessionUtil;
import com.atom.crm.workbench.dao.ClueDao;
import com.atom.crm.workbench.service.ClueService;

import java.util.List;

public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);

    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);


    @Override
    public List<User> getUserList() {

        List<User> userList = userDao.getUserList();

        return userList;
    }
}
