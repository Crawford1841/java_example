package org.example.spring.framework.beans.support;

import org.example.spring.framework.beans.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BeanDefinitionReader {
    //保存用户配置好的配置文件
    private Properties contextConfig = new Properties();
    //缓存从包路径下扫描的全类名，需要被注册的BeanClass
    private List<String> registryBeanClasses = new ArrayList<>();


    public BeanDefinitionReader(String... locations){
        //1、加载Properties文件
        doLoadConfig(locations[0]);
        //2、扫描相关类
        doScanner(contextConfig.getProperty("scanPackage"));
    }
    //扫描ClassPath下符合包路径规则所有的Class文件
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());

        for (File file : classPath.listFiles()) {
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else {
                //取反，减少代码嵌套
                if(!file.getName().endsWith(".class")){ continue; }

                //包名.类名  比如： com.gupaoedu.demo.DemoAction
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                //实例化，要用到  Class.forName(className);
                registryBeanClasses.add(className);
            }

        }
    }
    //根据contextConfigLocation的路径去ClassPath下找到对应的配置文件
    private void doLoadConfig(String contextConfigLocation) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:",""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<BeanDefinition> loadBeanDefinitions(){
        List<BeanDefinition> result = new ArrayList<BeanDefinition>();

        try {
            for (String className : registryBeanClasses) {
                Class<?> beanClass = Class.forName(className);

                //beanClass本身是接口的话，不做处理
                if(beanClass.isInterface()){ continue; }

                //1、默认类名首字母小写的情况
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));

                //2、如果是接口，就用实现类
                for (Class<?> i : beanClass.getInterfaces()) {
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String factoryClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(factoryClassName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;     //利用了ASCII码，大写字母和小写相差32这个规律
        return String.valueOf(chars);
    }
    public Properties getConfig() {
        return this.contextConfig;
    }
}
