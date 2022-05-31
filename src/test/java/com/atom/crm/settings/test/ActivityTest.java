package com.atom.crm.settings.test;

import com.atom.crm.utils.ServiceFactory;
import com.atom.crm.utils.UUIDUtil;
import com.atom.crm.workbench.domain.Activity;
import com.atom.crm.workbench.service.ActivityService;
import com.atom.crm.workbench.service.impl.ActivityServiceImpl;
import org.junit.Assert;
import org.junit.Test;

public class ActivityTest {

    @Test
    public void testSave(){

        Activity activity = new Activity();
        activity.setId(UUIDUtil.getUUID());
        activity.setName("测试一下");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.save(activity);

        Assert.assertEquals(flag, true);
    }

    @Test
    public void testUpdate(){
        System.out.println("123");
    }
}
