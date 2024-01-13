package org.example.demo;

import org.example.demo.mapper.UserMapper;
import org.example.framework.SqlSession;

/**
 * 1、存放参数和结果映射关系、存放SQL语句，我们需要定义一个配置类；
 * 2、执行对数据库的操作，处理参数和结果集的映射，创建和释放资源，我们需要定义一个执行器；
 * 3、有了这个执行器以后，我们不能直接调用它，而是定义一个给应用层使用的API，它可以根据SQL的
 * id找到SQL语句，交给执行器执行；
 * 4、如果由用户直接使用id查找SQL语句太麻烦了，我们干脆把存放SQL的命名空间定义成一个接口，把
 * SQL的id定义成方法，这样只要调用接口方法就可以找到要执行的SQL。刚好动态代理可以实现这个功
 * 能。这个时候我们需要引入一个代理类。
 */
public class TestMain {

    /**
     * TODO 实现流程
     * 1、定义配置类对象Configuration。里面要存放SQL语句，还有查询方法和结果映射的关系。
     * 2、定义应用层的APISlSession。在SlSession里面封装增删改查和操作事务的方法(selectOne0)。
     * 3、如果直接把Statement ID传给SalSession去执行SQL，会出现硬编码，我们决定把SQL语句的标识设计成一个接口+方法名 (Mapper接口) ，调用接口的方法就能找到SQL语句。
     * 4、这个需要代理模式实现，所以要创建一个实现了InvocationHandler的触发管理类MapperProxy。代理类在Configuration中通过]DK动态代理创建。
     * 5、有了代理对象之后，我们调用接口方法，就是调用触发管理器MapperProxy的invoke()方法
     * 6、代理对象的invoke0方法调用了SqlSession的selectOne0)。
     * 7、SqSession只是一个API，还不是真正的SQL执行者，所以接下来会调用执行器Executor的query0方法
     * 8、执行器Executor的query0方法里面就是对]DBC底层的Statement的封装，最终实现对数据库的操作，和结果的返回。
     * @param args
     */
    public static void main(String[] args) {
        SqlSession sqlSession = new SqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        System.out.println(mapper.selectOne(1));
    }
    /**
     * TODO 不足之处
     * 1、在Executor中，对参数、语句和结果集的处理是耦合的，没有实现职责分离;
     * 2、参数:没有实现对语句的预编译，只有简单的格式化 (format) ，效率不高，还存在SQL注入的风险
     * 3、语句执行: 数据库连接硬编码
     * 结果集: 还只能处理Blog类型，没有实现根据实体类自动映射。
     */
}
