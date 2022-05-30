package com.atom.crm.workbench.service.impl;

import com.atom.crm.settings.dao.UserDao;
import com.atom.crm.settings.domain.User;
import com.atom.crm.utils.SqlSessionUtil;
import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.dao.ActivityDao;
import com.atom.crm.workbench.dao.ActivityRemarkDao;
import com.atom.crm.workbench.domain.Activity;
import com.atom.crm.workbench.domain.ActivityRemark;
import com.atom.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {

    ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);

    ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);

    UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);


    @Override
    public boolean save(Activity activity) {
        System.out.println("业务层,保存市场活动");

        boolean flag = true;

        int count = activityDao.save(activity);

        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        /*
        * 要从dao层拿2个数据：total和Activity List
        * */

        int total = activityDao.getTotalByCondition(map);

        List<Activity> dataList = activityDao.getDataListByCondition(map);

        PaginationVO<Activity> vo = new PaginationVO<>();

        vo.setTotal(total);
        vo.setDataList(dataList);

        return vo;
    }

    @Override
    public boolean delete(String[] ids) {

        boolean flag = true;

        //查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByIds(ids);

        //实际删除的备注的数量
        int count2 = activityRemarkDao.deleteByIds(ids);

        if(count1 != count2){
            flag = false;
        }

        //删除市场活动
        int count = activityDao.deleteByIds(ids);

        if(count != ids.length){
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {

        //拿到用户列表
        List<User> uList = userDao.getUserList();

        //拿到本条市场活动信息
        Activity a = activityDao.getById(id);

        //打包成map，返回给控制器
        Map<String, Object> map = new HashMap<>();
        map.put("uList", uList);
        map.put("a", a);

        return map;

    }

    @Override
    public boolean update(Activity activity) {
        //数据库中被修改的数据量
        int count = activityDao.update(activity);

        boolean flag = true;

        //当count为1时，代表修改成功；否则修改失败。
        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Activity detail(String id) {

        Activity activity = activityDao.getById2(id);

        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {

        List<ActivityRemark> aRList = activityRemarkDao.getRemarkListByAid(activityId);

        return aRList;
    }

    @Override
    public boolean deleteRemark(String id) {

        boolean flag = true;

        int count = activityRemarkDao.deleteById(id);

        //count等于1，则说明修改成功；否则修改失败。
        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {

        boolean flag = true;

        int count = activityRemarkDao.saveRemark(ar);

        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {

        boolean flag = true;

        int count = activityRemarkDao.updateRemark(ar);

        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId02(String clueId) {

        List<Activity> activityList = activityDao.getActivityListByClueId02(clueId);

        return activityList;
    }

    @Override
    public List<Activity> GetActivityListByNameAndNotByClueId(Map<String, String> map) {

        List<Activity> activityList = activityDao.GetActivityListByNameAndNotByClueId(map);

        return activityList;
    }

    @Override
    public List<Activity> getActivityListByName(String name) {

        List<Activity> activityList = activityDao.getActivityListByName(name);

        return activityList;
    }
}
