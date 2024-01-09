package org.example.spring.framework.aop;

import org.example.spring.framework.aop.support.AdvisedSupport;

public class DefaultAopProxyFactory {
    public AopProxy createAopProxy(AdvisedSupport config)throws Exception{
        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length>0){
            return new JdkDynamicAopProxy(config);
        }
        return new CglibAopProxy();
    }
}
