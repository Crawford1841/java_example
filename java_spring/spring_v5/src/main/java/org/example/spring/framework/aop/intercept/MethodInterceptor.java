package org.example.spring.framework.aop.intercept;

public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
