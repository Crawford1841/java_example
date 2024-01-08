package org.example.http;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.List;
import java.util.Map;
import org.example.servlet.CustomRequest;

/**
 * 实现对Servlet规范的默认是实现
 */
public class HttpCustomRequest implements CustomRequest {

    private HttpRequest request;
    public HttpCustomRequest(HttpRequest request){
        this.request = request;
    }

    @Override
    public String getUri() {
        return request.getUri();
    }

    @Override
    public String getPath() {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        return decoder.path();
    }

    @Override
    public String getMethod() {
        return request.method().name();
    }

    @Override
    public Map<String, List<String>> getParameters() {
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        return decoder.parameters();
    }

    @Override
    public List<String> getParameters(String name) {
        return getParameters().get(name);
    }

    @Override
    public String getParamenter(String name) {
        List<String> parameters = getParameters(name);
        if(parameters == null || parameters.size() == 0){
            return null;
        }
        return parameters.get(0);
    }
}
