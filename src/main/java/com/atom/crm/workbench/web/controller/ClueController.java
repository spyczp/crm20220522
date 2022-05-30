package com.atom.crm.workbench.web.controller;

import com.atom.crm.settings.domain.User;
import com.atom.crm.utils.DateTimeUtil;
import com.atom.crm.utils.PrintJson;
import com.atom.crm.utils.ServiceFactory;
import com.atom.crm.utils.UUIDUtil;
import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.domain.Activity;
import com.atom.crm.workbench.domain.Clue;
import com.atom.crm.workbench.domain.ClueActivityRelation;
import com.atom.crm.workbench.service.ActivityService;
import com.atom.crm.workbench.service.ClueService;
import com.atom.crm.workbench.service.impl.ActivityServiceImpl;
import com.atom.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/workbench/clue/getUserList.do", "/workbench/clue/pageList.do", "/workbench/clue/save.do",
        "/workbench/clue/detail.do", "/workbench/clue/getActivityListByClueId.do", "/workbench/clue/unbound.do",
        "/workbench/clue/getActivityListByClueId02.do", "/workbench/clue/getActivityListByNameAndNotByClueId.do",
        "/workbench/clue/bound.do", "/workbench/clue/getActivityListByName.do"})
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
        }else if("/workbench/clue/detail.do".equals(servletPath)){
            doDetail(request, response);
        }else if("/workbench/clue/getActivityListByClueId.do".equals(servletPath)) {
            doGetActivityListByClueId(request, response);
        }else if("/workbench/clue/unbound.do".equals(servletPath)){
            doUnbound(request, response);
        }else if("/workbench/clue/getActivityListByClueId02.do".equals(servletPath)){
            doGetActivityListByClueId02(request, response);
        }else if("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(servletPath)){
            doGetActivityListByNameAndNotByClueId(request, response);
        }else if("/workbench/clue/bound.do".equals(servletPath)){
            doBound(request, response);
        }else if("/workbench/clue/getActivityListByName.do".equals(servletPath)){
            doGetActivityListByName(request, response);
        }
    }

    private void doGetActivityListByName(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        * 1.获取浏览器提交的数据：name
        * 2.根据name到数据库查找市场活动
        * 3.把市场活动列表返回给浏览器 [{activity1},{activity2},{activity3}...]
        * */
        System.out.println("根据名称模糊查询市场活动列表");

        String name = request.getParameter("name");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> activityList = as.getActivityListByName(name);

        PrintJson.printJsonObj(response, activityList);
    }

    private void doBound(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        * 1.拿到浏览器提交的数据：clueId,activityId
        * 2.新建关联关系表id
        * 3.把数据保存到数据库
        * 4.返回{success: true/fasle} 给浏览器
        * */
        System.out.println("绑定线索和市场活动");
        String clueId = request.getParameter("clueId");
        String[] activityIds = request.getParameterValues("activityId");

        List<ClueActivityRelation> carList = new ArrayList<>();

        for(String activityId: activityIds){
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(clueId);
            car.setActivityId(activityId);
            carList.add(car);
        }

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.bound(carList);

        PrintJson.printJsonFlag(response, flag);
    }

    private void doGetActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("按名称条件查找市场活动，并过滤已关联的市场活动");
        /*
        * 1.拿到浏览器提交的数据：aname，clueId
        * 2.到数据库查询市场活动信息
        * 3.返回市场活动信息列表给浏览器（json）
        * */

        String aname = request.getParameter("aname");
        String clueId = request.getParameter("clueId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Map<String, String> map = new HashMap<>();
        map.put("aname", aname);
        map.put("clueId", clueId);

        List<Activity> activityList = as.GetActivityListByNameAndNotByClueId(map);

        PrintJson.printJsonObj(response, activityList);
    }

    private void doGetActivityListByClueId02(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        String clueId = request.getParameter("clueId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<Activity> activityList = as.getActivityListByClueId02(clueId);

        PrintJson.printJsonObj(response, activityList);
    }

    private void doUnbound(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("解除线索和市场活动的关联关系");
        /*
        * 1.拿到浏览器提交的数据：关联关系id
        * 2.到关联关系表中删除数据
        * 3.给浏览器返回：{"success": true/false}
        * */
        String id = request.getParameter("id");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag = cs.unbound(id);

        PrintJson.printJsonFlag(response, flag);
    }

    private void doGetActivityListByClueId(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        * 1.拿到浏览器提交的数据：clueId
        * 2.到数据库查询市场活动信息列表
        * 3.给浏览器返回数据：[{activity1},{activity2},{activity3},...]
        * */
        String clueId = request.getParameter("clueId");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        List<Activity> activityList = cs.GetActivityListByClueId(clueId);

        PrintJson.printJsonObj(response, activityList);
    }

    private void doDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("查询线索的详细信息");
        /*
        * 1.拿到浏览器提交的数据：id
        * 2.根据id在数据库拿到线索的详细信息
        * 3.用请求转发，把线索数据保存到请求域中。
        * 4.浏览器通过el表达式展示数据。
        * */

        String id = request.getParameter("id");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        Clue clue = cs.getById(id);

        request.setAttribute("clue", clue);

        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request, response);
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
