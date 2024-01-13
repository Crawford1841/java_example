package org.example.framework;


/**
 * 对外提供操作接口
 */
public class SqlSession {

    private Configuration configuration;
    private Executor executor;

    public SqlSession() {
        this.configuration = new Configuration();
        this.executor = new Executor();
    }

    /**
     * 提供单条查询的方法
     *    通过Executor来执行
     * @param <T>
     * @return
     */
    public <T> T selectOne(String statementId,Object parameter){
        String sql = Configuration.sqlMappings.getString(statementId);
        System.out.println(sql);
        return executor.query(sql,parameter);
    }

    /**
     * 获取Mapper接口的代理对象
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class clazz){
        return configuration.getMapper(clazz,this);
    }

}
