package org.example.spring.framework.core;
/*
 * 创建对象工厂的最顶层的接口
 */
public interface BeanFactory {

    Object getBean(Class beanClass);

    Object getBean(String beanName);
}
