package org.example.servlet;

import java.util.List;
import java.util.Map;

/**
 * 定义Servlet规范
 */
public interface CustomRequest {
    //获取URI，包含请求参数，即问号后的内容（那么如果是post请求，参数不在问号后，如何处理呢？）
    String getUri();
    //获取请求路径，其不包含请求参数
    String getPath();
    //获取请求方法(Get、Post等)
    String getMethod();
    //获取所有请求参数
    Map<String, List<String>> getParameters();
    //获取指定名称的请求参数
    List<String> getParameters(String name);
    //获取指定名称的请求参数的第一个值
    String getParamenter(String name);
}
