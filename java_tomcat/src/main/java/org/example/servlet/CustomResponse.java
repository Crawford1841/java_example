package org.example.servlet;

/**
 * Servlet响应规范
 */
public interface CustomResponse {
    //将响应写入到Channel
    void write(String content)throws Exception;
}
