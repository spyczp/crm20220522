package com.atom.crm.workbench.service.impl;

import com.atom.crm.settings.dao.UserDao;
import com.atom.crm.settings.domain.User;
import com.atom.crm.utils.SqlSessionUtil;
import com.atom.crm.vo.PaginationVO;
import com.atom.crm.workbench.dao.ClueActivityRelationDao;
import com.atom.crm.workbench.dao.ClueDao;
import com.atom.crm.workbench.domain.Activity;
import com.atom.crm.workbench.domain.Clue;
import com.atom.crm.workbench.domain.ClueActivityRelation;
import com.atom.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);

    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);


    @Override
    public List<User> getUserList() {

        List<User> userList = userDao.getUserList();

        return userList;
    }

    @Override
    public PaginationVO<Clue> getByCondition(Map<String, Object> map) {

        int count = clueDao.getCountByCondition(map);

        List<Clue> clueList = clueDao.getByCondition(map);

        PaginationVO<Clue> paginationVO = new PaginationVO<>();

        paginationVO.setTotal(count);
        paginationVO.setDataList(clueList);

        return paginationVO;
    }

    @Override
    public boolean save(Clue clue) {

        boolean flag = true;

        int count = clueDao.save(clue);

        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Clue getById(String id) {

        Clue clue = clueDao.getById(id);

        return clue;
    }

    @Override
    public List<Activity> GetActivityListByClueId(String clueId) {

        List<Activity> activityList = clueActivityRelationDao.GetActivityListByClueId(clueId);

        return activityList;
    }

    @Override
    public boolean unbound(String id) {

        boolean flag = true;

        int count = clueActivityRelationDao.deleteById(id);

        if(count != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public boolean bound(List<ClueActivityRelation> carList) {

        boolean flag = true;
        int count = 0;


        for(ClueActivityRelation car: carList){
            clueActivityRelationDao.bound(car);
            count++;
        }

        if(count != carList.size()){
            flag = false;
        }

        return flag;
    }
}
