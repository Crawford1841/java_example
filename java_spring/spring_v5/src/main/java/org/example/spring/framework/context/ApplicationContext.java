package org.example.spring.framework.context;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/6 23:47
 */

import org.example.spring.framework.annotation.Autowired;
import org.example.spring.framework.annotation.Controller;
import org.example.spring.framework.annotation.Service;
import org.example.spring.framework.aop.DefaultAopProxyFactory;
import org.example.spring.framework.beans.BeanWrapper;
import org.example.spring.framework.beans.config.BeanDefinition;
import org.example.spring.framework.beans.support.BeanDefinitionReader;
import org.example.spring.framework.beans.support.DefaultListableBeanFactory;
import org.example.spring.framework.aop.config.AopConfig;
import org.example.spring.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.example.spring.framework.aop.support.AdvisedSupport;

/**
 * 实现Spring中的Application上下文的功能
 */
public class ApplicationContext implements BeanFactory {

    private DefaultListableBeanFactory registry = new DefaultListableBeanFactory();

    //AOP代理工厂
    private DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();

    //循环依赖的表示，当前正在创建的BeanName
    private Set<String> singletonsCurrentlyInCreatetion = new HashSet<>();

    //一级缓存：保存成熟的Bean
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    //二级缓存：保存早期的Bean
    private Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();

    //三级缓存（终极缓存）
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BeanWrapper>();

    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    private BeanDefinitionReader reader;

    public ApplicationContext(String... configLocations) {
        try {
            //1、加载配置文件
            reader = new BeanDefinitionReader(configLocations);

            //2、解析配置文件，将所有的配置信息封装成BeanDefinition对象
            List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

            //3、所有的配置信息缓存起来
            this.registry.doRegistBeanDefinition(beanDefinitions);

            //4、加载非延时加载的所有Bean
            doLoadInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doLoadInstance() {
        //循环调用getBean的方法
        for (Map.Entry<String, BeanDefinition> entry : this.registry.beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            if (!entry.getValue().isLazyInit()) {
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

        //去一级、二级缓存中拿
        Object singleton = getSingleton(beanName, beanDefinition);
        if (singleton != null) {
            return singleton;
        }

        //标记bean正在创建
        if (!singletonsCurrentlyInCreatetion.contains(beanName)) {
            singletonsCurrentlyInCreatetion.add(beanName);
        }

        //2、反射实例化对象
        Object instance = instantiateBean(beanName, beanDefinition);

        //input to singletonObjects cache
        this.singletonObjects.put(beanName, instance);

        //3、将返回的Bean的对象封装成BeanWrapper
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        //4、执行依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);
        //5、保存到IOC容器中
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        return beanWrapper.getWrappedInstance();
    }

    private Object getSingleton(String beanName, BeanDefinition beanDefinition) {
        //先去一级缓存里面拿
        Object bean = singletonObjects.get(beanName);
        //如果一级缓存中没有，但是又有创建标识，说明就是循环依赖
        if (bean == null && singletonsCurrentlyInCreatetion.contains(bean)) {
            bean = earlySingletonObjects.get(beanName);
            //如果二级缓存中也没有，则去三级缓存中拿
            if (bean == null) {
                bean = instantiateBean(beanName, beanDefinition);

                //将创建出来的对象重新放入到二级缓存中
                earlySingletonObjects.put(beanName, bean);
            }
        }
        return bean;
    }

    private void populateBean(String beanName, BeanDefinition beanDefinition, BeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        if (!(clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class))) {
            return;
        }
        //忽略字段的修饰符，不管是private/protected / public / default
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowriteBeanName = autowired.value().trim();
            if ("".equals(autowriteBeanName)) {
                autowriteBeanName = field.getType().getName();
            }
            //强制访问
            field.setAccessible(true);
            //if (this.factoryBeanInstanceCache.get(autowriteBeanName) == null) {
            //    continue;
            //}
            //相当于 demoAction.demoService = ioc.get("com.gupaoedu.demo.service.IDemoService");
            try {
                //field.set(instance, this.factoryBeanInstanceCache.get(autowriteBeanName).getWrappedInstance());
                /**
                 * 添加循环依赖的注入
                 */
                field.set(instance, getBean(autowriteBeanName));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    private Object instantiateBean(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton() && this.factoryBeanObjectCache.containsKey(beanName)) {
            return this.factoryBeanObjectCache.get(beanName);
        }

        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();
            //如果是代理对象则触发AOP的逻辑
            //==================AOP开始=========================
            //如果满足条件，就直接返回Proxy对象
            //1、加载AOP的配置文件
            AdvisedSupport config = instantionAopConfig(beanDefinition);
            config.setTargetClass(clazz);
            config.setTarget(instance);
            //判断规则，要不要生成代理类，如果要就覆盖原生对象
            //如果不要就不做任何处理，返回原生对象
            /**
             * TODO 加入AOP后，又重新导致了循环依赖的问题，如何解决呢？
             */
            if(config.pointCutMath()){
                instance = proxyFactory.createAopProxy(config).getProxy();
            }

            this.factoryBeanObjectCache.put(beanName, instance);
            this.factoryBeanObjectCache.put(clazz.getName(), instance);
            for (Class<?> i : clazz.getInterfaces()) {
                this.factoryBeanObjectCache.put(i.getName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * 简化配置，Spring原版是XML解析，这里我们直接采用Properties
     * @param beanDefinition
     * @return
     */
    private AdvisedSupport instantionAopConfig(BeanDefinition beanDefinition) {
        AopConfig config = new AopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);
    }

    public int getBeanDefinitionCount() {
        return this.registry.beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.registry.beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
