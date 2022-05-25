package com.atom.crm.workbench.web.controller;

import com.atom.crm.settings.domain.User;
import com.atom.crm.utils.DateTimeUtil;
import com.atom.crm.utils.PrintJson;
import com.atom.crm.utils.ServiceFactory;
import com.atom.crm.utils.UUIDUtil;
import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.domain.Clue;
import com.atom.crm.workbench.service.ClueService;
import com.atom.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/workbench/clue/getUserList.do", "/workbench/clue/pageList.do", "/workbench/clue/save.do"})
public class ClueController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("进入到线索控制器");

        String servletPath = request.getServletPath();

        if("/workbench/clue/getUserList.do".equals(servletPath)){
            doGetUserList(request, response);
        }else if("/workbench/clue/pageList.do".equals(servletPath)){
            doPageList(request, response);
        }else if("/workbench/clue/save.do".equals(servletPath)){
            doSave(request, response);
        }
    }

    private void doSave(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException  {
        System.out.println("保存线索到数据库");

        /*
        * 1.拿到浏览器提交的数据
        * 2.封装数据到Clue对象
        * 3.保存数据到数据库。
        * 4.返回{"success": true/false} 给浏览器
        * */

        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String appellation = request.getParameter("appellation");
        String fullname = request.getParameter("fullname");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        String id = UUIDUtil.getUUID();

        User user = (User) request.getSession(false).getAttribute("user");
        String createBy = user.getName();

        String createTime = DateTimeUtil.getSysTime();

        Clue clue = new Clue();
        clue.setId(id);
        clue.setFullname(fullname);
        clue.setAppellation(appellation);
        clue.setOwner(owner);
        clue.setCompany(company);
        clue.setJob(job);
        clue.setEmail(email);
        clue.setPhone(phone);
        clue.setWebsite(website);
        clue.setMphone(mphone);
        clue.setState(state);
        clue.setSource(source);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setContactSummary(contactSummary);
        clue.setNextContactTime(nextContactTime);
        clue.setAddress(address);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.save(clue);

        PrintJson.printJsonFlag(response, flag);
    }

    private void doPageList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("按条件查询线索，条件为空则查询所有线索。");
        /*
        * 1.从浏览器拿到条件数据
        * 2.把数据封装到Clue对象
        * 3.-->业务层-->dao层-->数据库 根据条件拿数据
        * 4.返回到控制器的数据是一个vo对象 PaginationVO
        * 5.把vo转换成json数据返回给浏览器。 {"total": 总条数, "dataList": [{clue1},{clue2},{clue3}...]}
        * */
        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String phone = request.getParameter("phone");
        String owner = request.getParameter("owner");
        String mphone = request.getParameter("mphone");
        String source = request.getParameter("source");
        String state = request.getParameter("state");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");

        Integer pageNo = Integer.valueOf(pageNoStr);
        Integer pageSize = Integer.valueOf(pageSizeStr);

        //limit 0, 2
        //limit skipCount, pageSize
        int skipCount = (pageNo - 1) * pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("fullname", fullname);
        map.put("company", company);
        map.put("phone", phone);
        map.put("owner", owner);
        map.put("mphone", mphone);
        map.put("source", source);
        map.put("state", state);
        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        PaginationVO<Clue> paginationVO = cs.getByCondition(map);

        PrintJson.printJsonObj(response, paginationVO);
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
