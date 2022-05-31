package com.atom.crm.workbench.web.controller;

import com.atom.crm.settings.domain.User;
import com.atom.crm.settings.service.UserService;
import com.atom.crm.settings.service.impl.UserServiceImpl;
import com.atom.crm.utils.PrintJson;
import com.atom.crm.utils.ServiceFactory;
import com.atom.crm.workbench.service.CustomerService;
import com.atom.crm.workbench.service.CustomerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/workbench/transaction/getUserList.do", "/workbench/transaction/getCustomerName.do"})
public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("进入到交易控制器");
        String servletPath = request.getServletPath();

        if("/workbench/transaction/getUserList.do".equals(servletPath)){
            doGetUserList(request, response);
        }else if("/workbench/transaction/getCustomerName.do".equals(servletPath)){
            doGetCustomerName(request, response);
        }
    }

    private void doGetCustomerName(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("获取所有客户的名称(按照客户名称模糊查询)");

        String name = request.getParameter("name");

        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());

        List<String> customerNameList =  cs.getCustomerName(name);

        //[客户名称1,客户名称2,客户名称3,...]
        PrintJson.printJsonObj(response, customerNameList);
    }

    private void doGetUserList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("获取用户列表");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = us.getUserList();

        request.setAttribute("userList", userList);

        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);
    }
}
