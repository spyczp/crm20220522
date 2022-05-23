package com.atom.crm.settings.service.impl;

import com.atom.crm.settings.dao.DicTypeDao;
import com.atom.crm.settings.dao.DicValueDao;
import com.atom.crm.settings.domain.DicValue;
import com.atom.crm.settings.service.DicService;
import com.atom.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {

    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);

    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<String, List<DicValue>> getAll() {
        /*
        * 1.创建一个map
        * 2.从数据库中拿所有的dicType code
        * 3.根据code查询每个code下所有的dicvalue
        * 4.把数据保存到map中。Map：{"dicCode1": dicValList2, "dicCode2": dicValList2, "dicCode3": dicValList3...}
        * */
        Map<String, List<DicValue>> map = new HashMap<>();

        List<String> dicTypeCodeList = dicTypeDao.getAllCode();

        for(String dicTypeCode: dicTypeCodeList){

            List<DicValue> dicValueList = dicValueDao.getByCode(dicTypeCode);

            map.put(dicTypeCode, dicValueList);
        }

        return map;
    }
}
