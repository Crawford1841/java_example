package org.example.spring.framework.aop.aspect;

import java.lang.reflect.Method;

public abstract class AbstractAspectJAdvice implements Advice{
    private Object aspect;
    private Method adviceMethod;
    private String throwName;

    public AbstractAspectJAdvice(Object aspect,Method adviceMethod){
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }

    protected Object invokeAdviceMethod(
            JoinPoint joinPoint, Object returnValue, Throwable ex)
            throws Throwable {
        Class<?> [] paramTypes = this.adviceMethod.getParameterTypes();
        if(null == paramTypes || paramTypes.length == 0){
            return this.adviceMethod.invoke(aspect);
        }else {
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] == JoinPoint.class) {
                    args[i] = joinPoint;
                } else if (paramTypes[i] == Throwable.class) {
                    args[i] = ex;
                } else if (paramTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.adviceMethod.invoke(aspect, args);
        }
    }
}
