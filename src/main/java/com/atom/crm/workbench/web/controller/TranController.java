package com.atom.crm.workbench.web.controller;

import com.atom.crm.settings.domain.User;
import com.atom.crm.settings.service.UserService;
import com.atom.crm.settings.service.impl.UserServiceImpl;
import com.atom.crm.utils.DateTimeUtil;
import com.atom.crm.utils.PrintJson;
import com.atom.crm.utils.ServiceFactory;
import com.atom.crm.utils.UUIDUtil;
import com.atom.crm.workbench.domain.Tran;
import com.atom.crm.workbench.service.CustomerService;
import com.atom.crm.workbench.service.TranService;
import com.atom.crm.workbench.service.impl.CustomerServiceImpl;
import com.atom.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Provider;
import java.util.List;

@WebServlet({"/workbench/transaction/getUserList.do", "/workbench/transaction/getCustomerName.do", "/workbench/transaction/save.do",
            "/workbench/transaction/getTransactionList.do"})
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
        }else if("/workbench/transaction/save.do".equals(servletPath)){
            doSave(request, response);
        }else if("/workbench/transaction/getTransactionList.do".equals(servletPath)){
            doGetTransactionList(request, response);
        }
    }

    private void doGetTransactionList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("获取所有交易数据");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<Tran> tranList = ts.getTransactionList();

        //[{交易1},{交易2},{交易3},...]
        PrintJson.printJsonObj(response, tranList);
    }

    private void doSave(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("创建交易");

        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        User user = (User) request.getSession(false).getAttribute("user");
        String createBy = user.getName();

        String createTime = DateTimeUtil.getSysTime();

        Tran tran = new Tran();
        tran.setId(UUIDUtil.getUUID());
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(tran, customerName);

        if(flag){

            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");

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
