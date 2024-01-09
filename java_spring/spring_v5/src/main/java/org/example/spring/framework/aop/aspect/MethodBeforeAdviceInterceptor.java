package org.example.spring.framework.aop.aspect;

import java.lang.reflect.Method;
import org.example.spring.framework.aop.intercept.MethodInterceptor;
import org.example.spring.framework.aop.intercept.MethodInvocation;

public class MethodBeforeAdviceInterceptor extends AbstractAspectJAdvice implements MethodInterceptor {
    private JoinPoint point;
    public MethodBeforeAdviceInterceptor(Object aspect, Method adviceMethod) {
        super(aspect,adviceMethod);
    }

    public void before(Method method,Object[] arguments,Object aThis){
        try {
            invokeAdviceMethod(this.point,null,null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        point = invocation;
        this.before(invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return invocation.proceed();
    }
}
