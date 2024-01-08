package org.example.servlet;

/**
 * 定义Servlet规范
 */
public abstract class Servlet {
    //处理http的get请求
    public abstract void doGet(CustomRequest request, CustomResponse response) throws Exception;
    //处理http的post请求
    public abstract void doPost(CustomRequest request, CustomResponse response)throws Exception;
}
