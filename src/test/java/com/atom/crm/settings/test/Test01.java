package com.atom.crm.settings.test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test01 {
    public static void main(String[] args) {

        String expireTime = "2022-05-13 14:19:19";

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String sysTime = sdf.format(date);

        System.out.println(sysTime);

        //大于0，当前系统时间大于失效时间
        //小于0，当前系统时间小于失效时间
        int count = sysTime.compareTo(expireTime);
        System.out.println(count);

    }
}
