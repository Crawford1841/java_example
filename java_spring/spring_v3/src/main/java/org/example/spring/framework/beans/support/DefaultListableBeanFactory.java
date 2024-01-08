package org.example.spring.framework.beans.support;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.spring.framework.beans.config.BeanDefinition;
import org.example.spring.framework.core.BeanFactory;

public class DefaultListableBeanFactory implements BeanFactory {

    public Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public Object getBean(Class beanClass) {
        return null;
    }

    @Override
    public Object getBean(String beanName) {
        return null;
    }

    public void doRegistBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exists!!!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }
}
