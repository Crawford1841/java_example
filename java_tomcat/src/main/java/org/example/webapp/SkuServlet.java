package org.example.webapp;

import org.example.servlet.CustomRequest;
import org.example.servlet.CustomResponse;
import org.example.servlet.Servlet;

/**
 * 业务方法
 */
public class SkuServlet extends Servlet {

    @Override
    public void doGet(CustomRequest request, CustomResponse response) throws Exception {
        String uri = request.getUri();
        String path = request.getPath();
        String method = request.getMethod();
        String name  = request.getParamenter("name");
        String content = "uri="+uri+"\n"+"path="+path+"\n"+"method="+method+"\n"+"param = "+name;
        response.write(content);
    }

    @Override
    public void doPost(CustomRequest request, CustomResponse response) throws Exception {
        doGet(request, response);
    }
}
