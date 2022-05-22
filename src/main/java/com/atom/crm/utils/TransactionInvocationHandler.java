package com.atom.crm.utils;

import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TransactionInvocationHandler implements InvocationHandler {

    //张三
    private Object target;

    public TransactionInvocationHandler(Object target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /*
        1.李四打开会话
        2.张三业务方法进行
        3.李四提交事务
        4.异常回滚事务
        5.释放资源
        * */
        Object obj = null; //存放张三方法执行结束后的返回值
        SqlSession sqlSession = null;

        try{
            sqlSession = SqlSessionUtil.getSqlSession();

            //args是方法执行时传入的参数
            obj = method.invoke(target, args);

            sqlSession.commit();
        }catch (Exception e){
            sqlSession.rollback();
            e.printStackTrace();

            //处理的是什么异常，继续往上抛什么异常
            throw e.getCause();
        }finally {
            SqlSessionUtil.closeSqlSession(sqlSession);
        }

        return obj;
    }

    //创建代理对象
    public Object getProxy(){
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
    }
}
