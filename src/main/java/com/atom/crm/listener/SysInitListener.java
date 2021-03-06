package com.atom.crm.listener;

import com.atom.crm.settings.domain.DicValue;
import com.atom.crm.settings.service.DicService;
import com.atom.crm.settings.service.impl.DicServiceImpl;
import com.atom.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.*;

@WebListener
public class SysInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("上下文域对象创建了");

        //获取上下文域对象
        ServletContext application = sce.getServletContext();

        /*
        * 在这里，我们需要把数据字典分门别类的保存到上下文域中
        * 1.分门别类的数据字典从业务层获取。格式：Map：{"dicCode1": dicValList2, "dicCode2": dicValList2, "dicCode3": dicValList3...}
        * 2.在上下文域中，以key为dicCode，value为dicValList来创建域属性。把数据字典保存到上下文域中。
        * 3.注意：web层调用业务层，都是使用动态代理来完成。
        * */
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());

        Map<String, List<DicValue>> map = ds.getAll();

        Set<String> dicCodeList = map.keySet();

        System.out.println("服务器开始加载数据字典");

        for(String dicCode: dicCodeList){
            //dicCode:appellation(例子)
            application.setAttribute(dicCode, map.get(dicCode));

        }

        System.out.println("数据字典加载完毕");

        System.out.println("服务器开始加载阶段-可能性数据");

        Map<String, String> pMap = new HashMap<>();

        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");

        Enumeration<String> stageKeys = rb.getKeys();

        while (stageKeys.hasMoreElements()) {

            String stage = stageKeys.nextElement();
            String possibility = rb.getString(stage);

            //{"阶段1": 可能性1, "阶段2": 可能性2, "阶段3": 可能性3, "阶段4": 可能性4, "阶段5": 可能性5, ...}
            pMap.put(stage, possibility);
        }

        application.setAttribute("pMap", pMap);

        System.out.println("阶段-可能性数据加载完毕");
    }
}
