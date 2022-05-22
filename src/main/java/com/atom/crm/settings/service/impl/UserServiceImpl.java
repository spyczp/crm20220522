package com.atom.crm.settings.service.impl;

import com.atom.crm.exception.UserLoginException;
import com.atom.crm.settings.dao.UserDao;
import com.atom.crm.settings.domain.User;
import com.atom.crm.settings.service.UserService;
import com.atom.crm.utils.DateTimeUtil;
import com.atom.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {

    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws UserLoginException {

        System.out.println("进入到业务层");

        /*
        * 1.把用户名和密码打包成map，传递给dao层，在数据库查找用户信息
        * 2.判断：
        *   用户为空，则说明浏览器提交的账号密码错误。
        *   否则，验证以下信息：失效时间，锁定状态，ip地址
        * */
        Map<String, String> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);

        User user = userDao.login(map);

        //用户为空，则说明浏览器提交的账号密码错误。
        if(user == null){
            throw new UserLoginException("账号密码错误");
        }

        //验证失效时间
        String expireTime = user.getExpireTime();
        String sysTime = DateTimeUtil.getSysTime();
        if(expireTime.compareTo(sysTime) < 0){
            throw new UserLoginException("账号已失效");
        }

        //验证锁定状态
        String lockState = user.getLockState();
        if("0".equals(lockState)){
            throw new UserLoginException("账号已锁定");
        }

        //验证ip地址
        String allowIps = user.getAllowIps();
        if(!allowIps.contains(ip)){
            throw new UserLoginException("当前网络不允许登录");
        }

        return user;
    }

    @Override
    public List<User> getUserList() {
        //调用dao层，获取用户信息列表

        List<User> userList = userDao.getUserList();

        return userList;
    }
}
