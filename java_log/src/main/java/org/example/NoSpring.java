package org.example;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.example.analysis.Log;
import org.example.mapper.LogMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class NoSpring {
    private static SqlSessionFactory sqlSessionFactory = initSqlSessionFactory();

    public static void main(String[] args) {
        //初始化
        SqlSession session = sqlSessionFactory.openSession(true);
        //创建mapper对象
        LogMapper logMapper = session.getMapper(LogMapper.class);
        List<File> list = FileUtil.loopFiles("E:\\新建文件夹\\2024-10-27.log");
        for(int i=0;i<list.size();i++){
            File item = list.get(i);
            List<String> strings = FileUtil.readUtf8Lines(item);
            find(strings,logMapper);
        }
    }

    private static void find(List<String> logs,LogMapper logMapper){
        if(CollectionUtil.isEmpty(logs)){
            return;
        }

        logs.forEach(log->{
            // 正则表达式匹配响应时间
            String regex = "\\d+ ms";
            // 创建Pattern对象
            Pattern pattern = Pattern.compile(regex);
            // 创建Matcher对象
            Matcher matcher = pattern.matcher(log);
            // 查找并打印匹配结果
            if (matcher.find()) {
                //接口匹配
                String interRegex = "\"[A-Z]+ (/[^ ]*)";
                Pattern compile = Pattern.compile(interRegex);
                Matcher matcherInter = compile.matcher(log);
                matcherInter.find();
                String url = matcherInter.group(1);
                String parms = "";
                if(url.indexOf("?")>0){
                    parms = url.substring(url.lastIndexOf("?")+1);
                    url = url.substring(0,url.lastIndexOf("?"));
                }
                //响应时间
                Integer newMs = Integer.valueOf(matcher.group().replaceAll("ms", "").trim());
                StringBuilder sb = new StringBuilder();
                sb.append("接口："+url+"，");
                sb.append("参数："+parms+"，");
                sb.append("响应时间："+newMs+"ms");
                Log save_log = new Log();
                save_log.setMs(newMs);
                save_log.setParams(parms);
                save_log.setContent(sb.toString());
                save_log.setInterfaceName(url);
                if(url.indexOf("/travel")>0){
                    save_log.setCategory("travel");
                }else if(url.indexOf("/console")>0){
                    save_log.setCategory("console");
                }else{
                    save_log.setCategory("operator");
                }

                //插入
                logMapper.insert(save_log);
                System.out.println("结果: " +save_log);
            }
        });
    }

    //工厂方法
    public static SqlSessionFactory initSqlSessionFactory() {
        DataSource dataSource = dataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("Production", transactionFactory, dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        //在这里添加Mapper
        configuration.addMapper(LogMapper.class);
        configuration.setLogImpl(StdOutImpl.class);
        return new MybatisSqlSessionFactoryBuilder().build(configuration);
    }

    //连接进行
    public static DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://192.168.0.49:3306/basisdb?serverTimezone=GMT%2B8");
        dataSource.setUsername("root");
        dataSource.setPassword("root123");
        return dataSource;
    }

}
