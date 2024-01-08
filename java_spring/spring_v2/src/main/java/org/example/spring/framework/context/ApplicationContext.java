package org.example.spring.framework.context;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/6 23:47
 */

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.spring.framework.annotation.Autowired;
import org.example.spring.framework.annotation.Controller;
import org.example.spring.framework.annotation.Service;
import org.example.spring.framework.beans.BeanWrapper;
import org.example.spring.framework.beans.config.BeanDefinition;
import org.example.spring.framework.beans.support.BeanDefinitionReader;
import org.example.spring.framework.beans.support.DefaultListableBeanFactory;
import org.example.spring.framework.core.BeanFactory;

/**
 * 实现Spring中的Application上下文的功能
 */
public class ApplicationContext implements BeanFactory {

    private DefaultListableBeanFactory registry = new DefaultListableBeanFactory();

    //三级缓存（终极缓存）
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>();

    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    private BeanDefinitionReader reader;

    public ApplicationContext(String... configLocations){
        //1、加载配置文件
        reader = new BeanDefinitionReader(configLocations);
        try {
            //2、解析配置文件，将所有的配置信息封装成BeanDefinition对象
            List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
            //3、所有的配置信息缓存起来
            this.registry.doRegistBeanDefinition(beanDefinitions);
            //4、加载非延时加载的所有Bean
            doLoadInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doLoadInstance() {
        //循环调用getBean的方法
        for (Map.Entry<String,BeanDefinition> entry:this.registry.beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            if(!entry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }

    }

    @Override
    public Object getBean(Class beanClass) {
        return getBean(beanClass.getName());
    }

    @Override
    public Object getBean(String beanName) {
        //1、先拿到BeanDefinition配置信息
        BeanDefinition beanDefinition = registry.beanDefinitionMap.get(beanName);
        //2、反射实例化对象
        Object instance = instantiateBean(beanName,beanDefinition);
        //3、将返回的Bean的对象封装成BeanWrapper
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        //4、执行依赖注入
        populateBean(beanName,beanDefinition,beanWrapper);
        //5、保存到IOC容器中
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        return beanWrapper.getWrappedInstance();
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        if(!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))){
            return;
        }
        //忽略字段的修饰符，不管是private/protected / public / default
        for(Field field : clazz.getDeclaredFields()){
            if(!field.isAnnotationPresent(Autowired.class)){
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowriteBeanName = autowired.value().trim();
            if("".equals(autowriteBeanName)){
                autowriteBeanName =  field.getType().getName();
            }
            //强制访问
            field.setAccessible(true);
            if(this.factoryBeanInstanceCache.get(autowriteBeanName) == null){
                continue;
            }
            //相当于 demoAction.demoService = ioc.get("com.gupaoedu.demo.service.IDemoService");
            try {
                field.set(instance,this.factoryBeanInstanceCache.get(autowriteBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();
            //如果是代理对象则触发AOP的逻辑
            this.factoryBeanObjectCache.put(beanName,instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public int getBeanDefinitionCount(){
        return this.registry.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames(){
        return this.registry.beanDefinitionMap.keySet().toArray(new String[0]);
    }


}
