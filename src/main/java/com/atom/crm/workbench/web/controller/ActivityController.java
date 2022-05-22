package com.atom.crm.workbench.web.controller;

import com.atom.crm.settings.domain.User;
import com.atom.crm.settings.service.UserService;
import com.atom.crm.settings.service.impl.UserServiceImpl;
import com.atom.crm.utils.DateTimeUtil;
import com.atom.crm.utils.PrintJson;
import com.atom.crm.utils.ServiceFactory;
import com.atom.crm.utils.UUIDUtil;
import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.domain.Activity;
import com.atom.crm.workbench.domain.ActivityRemark;
import com.atom.crm.workbench.service.ActivityService;
import com.atom.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/workbench/activity/getUserList.do", "/workbench/activity/save.do", "/workbench/activity/pageList.do", "/workbench/activity/delete.do",
        "/workbench/activity/getUserListAndActivity.do", "/workbench/activity/update.do", "/workbench/activity/detail.do",
        "/workbench/activity/getRemarkListByAid.do", "/workbench/activity/deleteRemark.do", "/workbench/activity/saveRemark.do",
        "/workbench/activity/updateRemark.do"})
public class ActivityController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("进入市场活动控制器");

        String servletPath = request.getServletPath();

        if("/workbench/activity/getUserList.do".equals(servletPath)){
            doGetUserList(request, response);
        }else if("/workbench/activity/save.do".equals(servletPath)){
            doSave(request, response);
        }else if("/workbench/activity/pageList.do".equals(servletPath)){
            doPageList(request, response);
        }else if("/workbench/activity/delete.do".equals(servletPath)){
            doDel(request, response);
        }else if("/workbench/activity/getUserListAndActivity.do".equals(servletPath)){
            doGetUserListAndActivity(request, response);
        }else if("/workbench/activity/update.do".equals(servletPath)){
            doUpdate(request, response);
        }else if("/workbench/activity/detail.do".equals(servletPath)){
            doDetail(request, response);
        }else if("/workbench/activity/getRemarkListByAid.do".equals(servletPath)){
            doGetRemarkList(request, response);
        }else if("/workbench/activity/deleteRemark.do".equals(servletPath)){
            doDelRemark(request, response);
        }else if("/workbench/activity/saveRemark.do".equals(servletPath)){
            doSaveRemark(request, response);
        }else if("/workbench/activity/updateRemark.do".equals(servletPath)){
            doUpdateRemark(request, response);
        }
    }

    //更新备注信息
    private void doUpdateRemark(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        /*
        * 1.拿到浏览器提交的数据：id、noteContent
        * 2.生成数据：editTime, editBy, editFlag=1
        * 3.把修改后的备注信息提交到数据库更新
        * */

        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");

        String editTime = DateTimeUtil.getSysTime();

        User user = (User) request.getSession(false).getAttribute("user");
        String editBy = user.getName();
        String editFlag = "1";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        ar.setEditFlag(editFlag);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.updateRemark(ar);

        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("ar", ar);

        // {"success": true/false, "ar": {备注}}
        PrintJson.printJsonObj(response, map);
    }

    private void doSaveRemark(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        /*
        * 1.拿到浏览器提交的数据：noteContent、activityId
        * 2.创建UUID、createTime、createBy、editFlag=0
        * 3.把上面的数据封装到ActivityRemark对象中
        * 4.把对象传递给业务层
        * 5.业务层把对象数据传递给dao层
        * 6.dao层保存数据到数据库
        * 7.返回flag：true/false
        * 8.最后控制器返回{"success": true/false, "ar": {备注}}给浏览器
        * */
        System.out.println("新增备注信息");

        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        User user = (User) request.getSession(false).getAttribute("user");
        String createBy = user.getName();

        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String editFlag = "0";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);
        ar.setActivityId(activityId);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.saveRemark(ar);

        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("ar", ar);

        PrintJson.printJsonObj(response, map);
    }

    private void doDelRemark(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /**
         * 1.拿到浏览器提交的备注信息id
         * 2.找到这条备注信息，，删除这条备注信息
         * 3.返回{"success": true/false} 给浏览器
         */

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.deleteRemark(id);

        PrintJson.printJsonFlag(response, flag);
    }

    private void doGetRemarkList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /*根据市场活动id，取得备注信息列表
        * 1.拿到浏览器提交的数据：市场活动id，对应市场活动备注activityId
        * 2.拿到市场活动备注信息列表：[{备注1},{备注2},{备注3}...]
        * 3.返回给浏览器。
        * */
        String activityId = request.getParameter("activityId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<ActivityRemark> aRList = as.getRemarkListByAid(activityId);

        PrintJson.printJsonObj(response, aRList);
    }

    private void doDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        * 1.拿到浏览器提交的数据：市场活动id
        * 2.调用业务层，根据id查询市场活动信息
        * 3.拿到市场活动信息，返回给浏览器(请求转发)
        * */
        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Activity activity = as.detail(id);

        request.setAttribute("activity", activity);

        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request, response);
    }

    private void doUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        * 1.获取浏览器提交的数据
        * 2.把数据封装到activity对象中
        * 3.传递给业务层
        * 4.业务层调dao层，把数据更新到数据库
        * 5.业务层返回 true/false
        * */

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String editTime = DateTimeUtil.getSysTime();

        User user = (User) request.getSession(false).getAttribute("user");
        String editBy = user.getName();

        Activity activity = new Activity();
        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setEditTime(editTime);
        activity.setEditBy(editBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.update(activity);

        PrintJson.printJsonFlag(response, flag);
    }

    private void doGetUserListAndActivity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        * 1.拿到浏览器提交的数据：id
        * 2.从业务层拿数据：map：{"uList": [{所有者1},{所有者2},{所有者3}], "a": {市场活动}}
        * 3.转换成json数据，返回给浏览器。
        * */

        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Map<String, Object> map = as.getUserListAndActivity(id);

        PrintJson.printJsonObj(response, map);
    }

    /**
     * 删除市场活动和对应的市场活动备注
     * @param request
     * @param response
     */
    private void doDel(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
        * 1.拿到浏览器提交的数据。例子：id=8a30a2449273480d8acc1c69bf329193&id=1cedb28ddcda4fd79098b9f44a2395bd
        * 2.id是市场活动的id
        * 3.根据id查询市场活动备注的条目，在市场活动备注表中，activityId = tbl_activity.id
        * 4.删除市场活动备注。
        *   判断：当查询到的市场活动备注条数和删除的条数一致时，代表删除成功。否则，删除失败。
        * 5.删除市场活动
        *   判断方式和4类似。
        * 6.全部都删除成功，则业务层返回true，否则返回false
        * 7.给浏览器返回：{"success", true/false}
        * */

        String[] ids = request.getParameterValues("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.delete(ids);

        PrintJson.printJsonFlag(response, flag);
    }

    /**
     *
     * 查询所有市场活动信息+总记录数
     * @param request
     * @param response
     */
    private void doPageList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");

        Integer pageNo = Integer.valueOf(pageNoStr);
        Integer pageSize = Integer.valueOf(pageSizeStr);

        // limit 0, 5
        // limit skipCount, pageSize
        int skipCount = (pageNo-1) * pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("skipCount", skipCount);
        map.put("pageSize", pageSize);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        PaginationVO<Activity> vo =  as.pageList(map);

        //{"total": total, "dataList": dataList}-->{"total": 100, "dataList": [{市场活动1},{市场活动2},{市场活动3},...]}
        //每个市场活动就是一个Activity对象
        PrintJson.printJsonObj(response, vo);
    }

    /**
     * 新增市场活动信息
     * @param request
     * @param response
     */
    private void doSave(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("保存市场活动");
        /*
        * 1.拿到浏览器提交的数据。
        * 2.给id，createTime，createBy 创建值。
        * 3.把数据封装到Activity对象。
        * 4.把对象传递给业务层。
        * 5.业务层把对象传递给dao层，保存到数据库。
        * 6.操作成功，控制器给浏览器返回 {"success": true} 的json数据。
        * */
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        Activity activity = new Activity();

        activity.setId(id);
        activity.setOwner(owner);
        activity.setName(name);
        activity.setStartDate(startDate);
        activity.setEndDate(endDate);
        activity.setCost(cost);
        activity.setDescription(description);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);

        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = activityService.save(activity);

        PrintJson.printJsonFlag(response, flag);

    }


    /**
     *
     * 获取所有用户信息
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void doGetUserList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        /*
        * 调业务层，获取用户信息列表
        * 也是用代理对象
        * 把json数组格式的用户列表返回给浏览器
        * */

        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> userList = userService.getUserList();

        PrintJson.printJsonObj(response, userList);
    }
}
