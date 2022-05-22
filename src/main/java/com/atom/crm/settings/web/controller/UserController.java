package com.atom.crm.settings.web.controller;

import com.atom.crm.settings.domain.User;
import com.atom.crm.settings.service.UserService;
import com.atom.crm.settings.service.impl.UserServiceImpl;
import com.atom.crm.utils.MD5Util;
import com.atom.crm.utils.PrintJson;
import com.atom.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@WebServlet({"/settings/user/login.do"})
public class UserController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("进入到控制器中");

        String servletPath = request.getServletPath();

        if("/settings/user/login.do".equals(servletPath)){
            doLogin(request, response);
        }
    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
       /* 1.拿到浏览器提交的数据：loginAct、loginPwd
                密码加密
       *  2.获取请求中的信息：ip
       *  3.创建代理对象
       * */

        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        loginPwd = MD5Util.getMD5(loginPwd);

        String ip = request.getRemoteAddr();
        System.out.println("===========ip:" + ip);

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try{
            User user = userService.login(loginAct, loginPwd, ip);

            //程序到这里，表示账号密码验证正确，没有抛出异常。
            request.getSession().setAttribute("user", user);
            //{"success": true}
            PrintJson.printJsonFlag(response, true);
        }catch (Exception e){
            e.printStackTrace();

            String msg = e.getMessage();
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("msg", msg);

            //{"success": true, "msg": msg}
            PrintJson.printJsonObj(response, map);
        }
    }
}
