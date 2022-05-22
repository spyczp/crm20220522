package com.atom.crm.settings.test;

import com.atom.crm.utils.MD5Util;

public class Test03 {
    public static void main(String[] args) {

        String pwd = "123456";
        String md5Pwd = MD5Util.getMD5(pwd);

        System.out.println(md5Pwd);
    }
}
