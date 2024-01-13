package org.example.demo;

import org.example.demo.domain.User;
import org.example.demo.mapper.UserMapper;
import org.example.franework.session.DefaultSqlSession;
import org.example.franework.session.SqlSessionFactory;

/**
 * Mybatis_v1的代码优化目标：
 *  1、对Executor的职责进行细化;
 *  2、支持参数预编译;
 *  3、支持结果集的自动处理 (通过反射)。
 * Mybatis_v1需要功能增强的目标：
 *  1、在方法上使用注解配置SQL;
 *  2、查询带缓存功能;
 *  3、支持自定义插件。
 */
public class TestMain {

    /**
     * TODO 实现流程
     * 1、建立全局的数据库配置文件，对Executor进行职责划分
     * 2、Configuration进行细化
     *      2.1、创建MapperProxyFactory用来创建代理对象
     *      2.2、创建MapperResitry，维护接口和工厂类的映射关系
     *      2.3、创建SqlSessionFactory，用来创建SqlSession
     *      2.4、定义Executor接口和基本实现SimpleExecutor
     * 3、支持注解
     * 4、支持缓存
     *  插件需要几个必要的类
     *      Interceptor接口规范插件格式
     *      @Intercepts注解指定拦截的对象和方法
     *      InterceptorChain容纳解析的插件类
     *      Plugin可以产生代理对象，也是触发管理器,
     *      Invocation包装类，用来调用被拦截对象的方法
     * 5、支持插件
     *
     * @param args
     */
    public static void main(String[] args) {
        SqlSessionFactory factory = new SqlSessionFactory();
        DefaultSqlSession sqlSession = factory.build().openSqlSession();
        // 获取包含了h MapperProxy代理
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.selectOne(1);

        System.out.println("第一次查询: " + user);
        System.out.println();
        user = mapper.selectOne(1);
        System.out.println("第二次查询: " + user);
    }
    /**
     * TODO 不足之处
     * 1、不能返回List、Map;
     * 2、TypeHandler只能处理部分类型，如果能够处理所有类型的转换关系，和自定义类型就好了。缓存只有一级，只有一个全局开关，不能在单个方法上关闭(配置不灵活，properties不够用了) :
     * 3、缺失插入、删除、修改的注解
     * 4、插件对其他对象、指定方法的拦截，插件支持参数配置;
     * 5、细节考虑不足，异常处理有点粗暴;
     */
}
