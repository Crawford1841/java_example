package org.example.spring.framework.aop.aspect;

import java.lang.reflect.Method;
import org.example.spring.framework.aop.intercept.MethodInterceptor;
import org.example.spring.framework.aop.intercept.MethodInvocation;

public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice implements MethodInterceptor {

    private String throwName;

    public AspectJAfterThrowingAdvice(Object aspect, Method adviceMethod) {
        super(aspect, adviceMethod);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        }catch (Exception e){
            invokeAdviceMethod(invocation,null,e);
            throw e;
        }
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
