package org.example.spring.framework.aop.aspect;

import java.lang.reflect.Method;
import org.example.spring.framework.aop.intercept.MethodInterceptor;
import org.example.spring.framework.aop.intercept.MethodInvocation;

public class AfterReturningAdviceInterceptor extends AbstractAspectJAdvice implements MethodInterceptor {

    private JoinPoint point;

    public AfterReturningAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect,adviceMethod);
    }
    public void before(Method method, Object[] arguments, Object aThis) throws Throwable{
        invokeAdviceMethod(this.point,null,null);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        this.point = invocation;
        this.before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return invocation.proceed();
    }
}
