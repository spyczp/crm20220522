package com.atom.crm.utils;

public class ServiceFactory {

    //service:张三 ======> TransactionInvocationHandler的属性:target
    public static Object getService(Object service){

        TransactionInvocationHandler tih = new TransactionInvocationHandler(service);
        return tih.getProxy();

    }
}
