package com.atom.crm.workbench.dao;

import com.atom.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityDao {
    int save(Activity activity);

    int getTotalByCondition(Map<String, Object> map);

    List<Activity> getDataListByCondition(Map<String, Object> map);

    int deleteByIds(String[] ids);

    Activity getById(String id);

    int update(Activity activity);

    Activity getById2(String id);

    List<Activity> getActivityListByClueId02(String clueId);

    List<Activity> GetActivityListByNameAndNotByClueId(Map<String, String> map);
}
