package com.atom.crm.settings.dao;

import com.atom.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getByCode(String dicTypeCode);
}
