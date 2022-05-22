package com.atom.crm.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SqlSessionUtil {

    /*
    * 1.创建工厂建造器
    * 2.把mybatis-config.xml以流的形式引入
    * 3.建造工厂
    * 4.从本地线程池中获取sqlsession会话，若不存在则新建
    * */

    private static SqlSessionFactory sqlSessionFactory;
    private static ThreadLocal<SqlSession> t = new ThreadLocal<SqlSession>();

    //构造方法私有化，防止外部使用构造方法创建工具类对象
    private SqlSessionUtil(){}

    static{
        InputStream resource = null;
        try {
            resource = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            sqlSessionFactory = sqlSessionFactoryBuilder.build(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开SqlSession会话
     * @return
     */
    public static SqlSession getSqlSession(){
        SqlSession sqlSession = t.get();
        if(sqlSession == null){
            sqlSession = sqlSessionFactory.openSession();
            t.set(sqlSession);
        }
        return sqlSession;
    }

    /**
     * 释放资源
     * @param sqlSession
     */
    public static void closeSqlSession(SqlSession sqlSession){
        if(sqlSession != null){
            sqlSession.close();
            t.remove();
        }
    }
}
