package org.example.franework.binding;

import java.util.HashMap;
import java.util.Map;
import org.example.franework.session.DefaultSqlSession;

/**
 * 维护接口和工厂类的关系，用于获取MapperProxy代理对象
 * 工厂类指定了POJO类型，用于处理结果集返回
 */
public class MapperRegistry {

    // 接口和工厂类映射关系
    private final Map<Class<?>, MapperProxyFactory> knownMappers = new HashMap<>();

    /**
     * 在Configuration中解析接口上的注解时，存入接口和工厂类的映射关系
     * 此处传入pojo类型，是为了最终处理结果集的时候将结果转换为POJO类型
     *
     * @param clazz
     * @param pojo
     * @param <T>
     */
    public <T> void addMapper(Class<T> clazz, Class pojo){
        knownMappers.put(clazz, new MapperProxyFactory(clazz, pojo));
    }

    /**
     * 创建一个代理对象
     */
    public <T> T getMapper(Class<T> clazz, DefaultSqlSession sqlSession) {
        MapperProxyFactory proxyFactory = knownMappers.get(clazz);
        if (proxyFactory == null) {
            throw new RuntimeException("Type: " + clazz + " can not find");
        }
        return (T)proxyFactory.newInstance(sqlSession);
    }

}
