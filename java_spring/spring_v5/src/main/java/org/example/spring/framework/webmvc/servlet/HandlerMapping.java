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
    private Object controller;
    //必须把url放到HandlerMapping才好理解吧
    private Pattern pattern;  //正则 url占位符解析
    private Method method; //保存映射方法
    public Pattern getPattern() {
        return pattern;
    }

    public Method getMethod() {
        return method;
    }

    public Object getController() {
        return controller;
    }


    public HandlerMapping(Pattern pattern,Object controller, Method method) {
        this.controller = controller;
        this.pattern = pattern;
        this.method = method;

    }
}
