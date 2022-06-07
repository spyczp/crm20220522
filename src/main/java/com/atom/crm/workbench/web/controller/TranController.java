package com.atom.crm.workbench.web.controller;

import com.atom.crm.settings.domain.User;
import com.atom.crm.settings.service.UserService;
import com.atom.crm.settings.service.impl.UserServiceImpl;
import com.atom.crm.utils.DateTimeUtil;
import com.atom.crm.utils.PrintJson;
import com.atom.crm.utils.ServiceFactory;
import com.atom.crm.utils.UUIDUtil;
import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.domain.Tran;
import com.atom.crm.workbench.domain.TranHistory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/workbench/transaction/getUserList.do", "/workbench/transaction/getCustomerName.do", "/workbench/transaction/save.do",
            "/workbench/transaction/getTransactionList.do", "/workbench/transaction/detail.do", "/workbench/transaction/getTranHistory.do",
            "/workbench/tran/changeStage.do", "/workbench/transaction/getChart.do"})
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
        }else if("/workbench/transaction/detail.do".equals(servletPath)){
            doDetail(request, response);
        }else if("/workbench/transaction/getTranHistory.do".equals(servletPath)){
            doGetTranHistory(request, response);
        }else if("/workbench/tran/changeStage.do".equals(servletPath)){
            doChangeStage(request, response);
        }else if("/workbench/transaction/getChart.do".equals(servletPath)){
            doGetChart(request, response);
        }
    }

    private void doGetChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*
        * 给浏览器返回数据：{
        *               "total": 总交易数,
                        "dataList":
                                [{ "value": 交易数, "name": '阶段1' },
                                { "value": 交易数, "name": '阶段2' },
                                { "value": 交易数, "name": '阶段3' },...]
                         }
        * */

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        /*
        * map = { "total": 总交易数, "dataList": [map1, map2, map3] }
        * map1 = { "value": 交易数, "name": '阶段1' }
        * map2 = { "value": 交易数, "name": '阶段2' }
        * map3 = { "value": 交易数, "name": '阶段3' }
        * */
        Map<String, Object> map = ts.getChart();

        PrintJson.printJsonObj(response, map);
    }

    private void doChangeStage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        * 1.拿到浏览器提交的数据：
        *   data: {
				"id": "${tran.id}",
				"stage": stage,
				"money": "${tran.money}",
				"expectedDate": "${tran.expectedDate}",
			},
		*
		* 2.到数据库，tran表中修改阶段数据；到tran_history表中创建一条新的交易历史。
		* 3.返回{"success": true/false, "tran": tran}给浏览器。tran: {"属性1": 属性值1, "属性2": 属性值2, "属性3": 属性值3, ...}
        * */
        System.out.println("改变交易的阶段，同时创建交易历史");

        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");

        //这里createBy和createTime用作tran表的editBy和editTime；
        //用作tran_history的createBy和createTime。
        User user = (User) request.getSession(false).getAttribute("user");
        String editBy = user.getName();
        String editTime = DateTimeUtil.getSysTime();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.changeStage(t);

        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("tran", t);

        PrintJson.printJsonObj(response, map);
    }

    private void doGetTranHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("获取一笔交易的所有交易历史");

        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        List<TranHistory> tranHistoryList = ts.getTranHistoryByTranId(tranId);

        PrintJson.printJsonObj(response, tranHistoryList);
    }

    private void doDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        System.out.println("查询一条交易的详情");

        String id = request.getParameter("id");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran tran = ts.getById(id);

        request.setAttribute("tran", tran);

        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);
    }

    private void doGetTransactionList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("根据查询条件获取交易数据");

        /*
        * 1.获取浏览器提交的数据
        * 2.调业务层方法，把数据传递给业务层
        * 3.使用数据到数据库查询交易数据
        * 4.返回交易数据给浏览器
        *
        * */
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String transactionType = request.getParameter("transactionType");
        String source = request.getParameter("source");
        String contactsName = request.getParameter("contactsName");

        Integer pageNo = Integer.valueOf(pageNoStr);
        Integer pageSize = Integer.valueOf(pageSizeStr);

        int skipCount = (pageNo - 1) * pageSize;

        Map<String, Object> map = new HashMap<>();

        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);
        map.put("owner", owner);
        map.put("name", name);
        map.put("stage", stage);
        map.put("type", transactionType);
        map.put("source", source);
        map.put("customerName", customerName);
        map.put("contactsName", contactsName);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        PaginationVO<Tran> paginationVO = ts.getTransactionList(map);

        //{total:总条数, dataList:[{交易1},{交易2},{交易3},...]}
        PrintJson.printJsonObj(response, paginationVO);
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
