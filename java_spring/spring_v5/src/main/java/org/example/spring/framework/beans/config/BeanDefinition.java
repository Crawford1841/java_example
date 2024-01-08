package org.example.spring.framework.beans.config;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/6 23:49
 */
public class BeanDefinition {
    public boolean isLazyInit(){  return false; }
    public boolean isSingleton(){return true;}
    private String factoryBeanName; //beanName
    private String beanClassName;   //原生类的全类名

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
