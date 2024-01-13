package org.example.framework;

import java.lang.reflect.Proxy;
import java.util.ResourceBundle;

/**
 * 保存所有的配置信息
 */
public class Configuration {
    // 保存 属性文件信息
    public static final ResourceBundle sqlMappings;

    static {
        sqlMappings = ResourceBundle.getBundle("sql");
    }

    /**
     * 获取Mapper接口对应的代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class clazz ,SqlSession sqlSession){
        return (T) Proxy.newProxyInstance(Configuration.class.getClassLoader()
        ,new Class[]{clazz},new MapperProxy(sqlSession));
    }

}
