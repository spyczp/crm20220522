package com.atom.crm.workbench.service.impl;

import com.atom.crm.utils.SqlSessionUtil;
import com.atom.crm.workbench.dao.ClueDao;
import com.atom.crm.workbench.service.ClueService;

public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);


}
