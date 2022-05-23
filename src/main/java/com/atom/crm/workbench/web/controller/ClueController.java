package com.atom.crm.workbench.web.controller;

import com.atom.crm.settings.domain.User;
import com.atom.crm.utils.PrintJson;
import com.atom.crm.utils.ServiceFactory;
import com.atom.crm.workbench.service.ClueService;
import com.atom.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/workbench/clue/getUserList.do"})
public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("进入到线索控制器");

        String servletPath = request.getServletPath();

        if("/workbench/clue/getUserList.do".equals(servletPath)){
            doGetUserList(request, response);
        }
    }

    private void doGetUserList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * 1.创建动态代理
         * 2.调用业务层方法
         * 3.拿到浏览器需要的数据。
         * 4.把数据转成json格式，返回给浏览器：[{User1}, {User2}]
         *
         * */
        System.out.println("获取用户列表");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        List<User> userList = cs.getUserList();

        PrintJson.printJsonObj(response, userList);
    }
}
