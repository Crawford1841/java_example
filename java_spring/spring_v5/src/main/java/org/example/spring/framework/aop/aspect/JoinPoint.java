package org.example.spring.framework.aop.aspect;

import java.lang.reflect.Method;

public interface JoinPoint {
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key,Object value);

    Object getUserAttribute(String key);

}
