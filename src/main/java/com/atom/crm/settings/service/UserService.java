package com.atom.crm.settings.service;

import com.atom.crm.exception.UserLoginException;
import com.atom.crm.settings.domain.User;

import java.util.List;

public interface UserService {

    User login(String loginAct, String loginPwd, String ip) throws UserLoginException;

    List<User> getUserList();
}
