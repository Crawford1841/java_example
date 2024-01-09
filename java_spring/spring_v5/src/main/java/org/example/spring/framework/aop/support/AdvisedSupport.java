package org.example.spring.framework.aop.support;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.example.spring.framework.aop.aspect.AfterReturningAdviceInterceptor;
import org.example.spring.framework.aop.aspect.AspectJAfterThrowingAdvice;
import org.example.spring.framework.aop.aspect.MethodBeforeAdviceInterceptor;
import org.example.spring.framework.aop.config.AopConfig;

/**
 * 解析AOP配置的工具类
 */
public class AdvisedSupport {
    private AopConfig config;
    private Object target;
    private Class targetClass;
    private Pattern pointCutClassPattern;
    //    private Map<Method,Map<String,GPAdvice>> methodCache;
    private Map<Method, List<Object>> methodCache;

    public AdvisedSupport(AopConfig config){
        this.config = config;
    }

    /**
     * 解析配置文件的方法
     *
     */
    private void parse() {

        //把Spring的Excpress变成Java能够识别的正则表达式
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");


        //保存专门匹配Class的正则
        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));


        //享元的共享池
//        methodCache = new HashMap<Method, Map<String, Advice>>();
        methodCache = new HashMap<Method, List<Object>>();
        //保存专门匹配方法的正则
        Pattern pointCutPattern = Pattern.compile(pointCut);
        try{
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(),method);
            }

            for (Method method : this.targetClass.getMethods()) {
                String methodString = method.toString();
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pointCutPattern.matcher(methodString);
                if(matcher.matches()){
                    List<Object> advices = new LinkedList<Object>();

                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))){
                        advices.add(new MethodBeforeAdviceInterceptor(aspectClass.newInstance(),aspectMethods.get(config.getAspectBefore())));
                    }
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))){
                        advices.add(new AfterReturningAdviceInterceptor(aspectClass.newInstance(),aspectMethods.get(config.getAspectAfter())));
                    }
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))){
                        AspectJAfterThrowingAdvice advice = new AspectJAfterThrowingAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectAfterThrow()));
                        advice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(advice);
                    }
                    //跟目标代理类的业务方法和Advices建立一对多个关联关系，以便在Porxy类中获得
                    methodCache.put(method,advices);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws  Exception{

        // 从缓存中获取
        List<Object> cached = this.methodCache.get(method);
        // 缓存未命中，则进行下一步处理
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached = methodCache.get(m);
            this.methodCache.put(m, cached);
        }
        return cached;
    }


//    //根据一个目标代理类的方法，获得其对应的通知
//    public Map<String,GPAdvice> getAdvices(Method method, Object o) throws Exception {
//        //享元设计模式的应用
//        Map<String,GPAdvice> cache = methodCache.get(method);
//        if(null == cache){
//            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
//            cache = methodCache.get(m);
//            this.methodCache.put(m,cache);
//        }
//        return cache;
//    }

    //给ApplicationContext首先IoC中的对象初始化时调用，决定要不要生成代理类的逻辑
    public boolean pointCutMath() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }
}
