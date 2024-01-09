package org.example.spring.framework.aop;

/**
 * AOP的顶层接口设计
 */
public interface AopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
