package com.atom.crm.settings.test;

public class Test02 {
    public static void main(String[] args) {

        String ip = "192.168.1.3";

        String allowIps = "192.168.1.1,192.168.1.2";

        if(allowIps.contains(ip)){
            System.out.println("allow login");
        }else {
            System.out.println("forbit login");
        }
    }
}
