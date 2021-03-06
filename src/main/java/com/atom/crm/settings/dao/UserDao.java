package com.atom.crm.settings.dao;

import com.atom.crm.settings.domain.User;
import com.atom.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface UserDao {

    User login(Map<String, String> map);

    List<User> getUserList();

}
