package com.atom.crm.settings.service.impl;

import com.atom.crm.settings.dao.DicTypeDao;
import com.atom.crm.settings.dao.DicValueDao;
import com.atom.crm.settings.service.DicService;
import com.atom.crm.utils.SqlSessionUtil;

public class DicServiceImpl implements DicService {

    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);

    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);
}
