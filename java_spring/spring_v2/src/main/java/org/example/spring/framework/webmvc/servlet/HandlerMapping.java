package org.example.spring.framework.webmvc.servlet;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/6 23:45
 */

import org.example.spring.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 保存一个url和一个Method的关系
 */
public class HandlerMapping {
    //必须把url放到HandlerMapping才好理解吧
    private Pattern pattern;  //正则 url占位符解析
    private Method method; //保存映射方法
    private Class<?> [] paramTypes;

    public Pattern getPattern() {
        return pattern;
    }

    public Method getMethod() {
        return method;
    }


    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    //形参列表
    //参数的名字作为key,参数的顺序，位置作为值
    private Map<String,Integer> paramIndexMapping;

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }

    public HandlerMapping(Pattern pattern, Method method) {
        this.pattern = pattern;
        this.method = method;

        paramTypes = method.getParameterTypes();

        paramIndexMapping = new HashMap<String, Integer>();
        putParamIndexMapping(method);
    }

    private void putParamIndexMapping(Method method){

        //提取方法中加了注解的参数
        //把方法上的注解拿到，得到的是一个二维数组
        //因为一个参数可以有多个注解，而一个方法又有多个参数
        Annotation[] [] pa = method.getParameterAnnotations();
        for (int i = 0; i < pa.length ; i ++) {
            for(Annotation a : pa[i]){
                if(a instanceof RequestParam){
                    String paramName = ((RequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        //提取方法中的request和response参数
        Class<?> [] paramsTypes = method.getParameterTypes();
        for (int i = 0; i < paramsTypes.length ; i ++) {
            Class<?> type = paramsTypes[i];
            if(type == HttpServletRequest.class ||
                    type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }
        }

    }
}
