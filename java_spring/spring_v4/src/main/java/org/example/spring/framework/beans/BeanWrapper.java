package org.example.spring.framework.beans;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/6 23:51
 */

public class BeanWrapper {
    private Object wrapperedInstance;
    private Class<?> wrappedClass;

    public BeanWrapper(Object instance) {
        this.wrapperedInstance = instance;
        this.wrappedClass = instance.getClass();
    }

    public Object getWrappedInstance(){
        return this.wrapperedInstance;
    }

    /**
     * 返回代理以后的class，可能会是$Proxy0
     * @return
     */
    public Class<?> getWrappedClass(){
        return this.wrappedClass;
    }
}

