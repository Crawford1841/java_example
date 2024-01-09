package org.example.spring.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import org.example.spring.framework.aop.intercept.MethodInvocation;
import org.example.spring.framework.aop.support.AdvisedSupport;

public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {
    private AdvisedSupport advised;
    public JdkDynamicAopProxy(AdvisedSupport config){
        this.advised = config;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());

        MethodInvocation mi = new MethodInvocation(proxy, this.advised.getTarget(), method, args, this.advised.getTargetClass(), chain);

        return mi.proceed();
    }
    //    private void invokeAdivce(GPAdvice advice) {
//        try {
//            advice.getAdviceMethod().invoke(advice.getAspect());
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public Object getProxy() {
        return getProxy(this.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }
}
