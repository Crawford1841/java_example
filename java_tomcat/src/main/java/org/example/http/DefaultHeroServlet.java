package org.example.http;

import org.example.servlet.CustomRequest;
import org.example.servlet.CustomResponse;
import org.example.servlet.Servlet;

/**
 * Tomcatv1中对Servlet规范的默认实现
 */
public class DefaultHeroServlet extends Servlet {

    @Override
    public void doGet(CustomRequest request, CustomResponse response)throws Exception {
        String uri = request.getUri();;
        String name = uri.substring(0,uri.indexOf("?"));
        response.write("404 - no this servlet ："+name);
    }

    @Override
    public void doPost(CustomRequest customRequest, CustomResponse customResponse)throws Exception {
        doGet(customRequest, customResponse);
    }
}
