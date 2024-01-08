package org.example.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.StringUtil;
import java.util.List;
import java.util.Map;
import org.example.servlet.CustomRequest;
import org.example.servlet.CustomResponse;

/**
 * 实现对Servlet规范的默认是实现
 */
public class HttpCustomResponse implements CustomResponse {
    private HttpRequest request;
    private ChannelHandlerContext context;
    public HttpCustomResponse(HttpRequest request,ChannelHandlerContext context){
        this.request = request;
        this.context = context;
    }
    @Override
    public void write(String content) throws Exception {
        //处理空的情况
        if(StringUtil.isNullOrEmpty(content)){
            return;
        }
        //创建响应对象
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                //根据响应体内容大型为response对象分配存储空间
                Unpooled.wrappedBuffer(content.getBytes("UTF-8")));

        //获取响应头
        HttpHeaders headers = response.headers();
        //设置响应体类型 (apache tomcat是如何处理不同的响应类型的)
        headers.set(HttpHeaderNames.CONTENT_TYPE,"text/json");
        //设置响应体长度
        headers.set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());
        //设置缓存过期事件
        headers.set(HttpHeaderNames.EXPIRES,0);
        //若http请求是长连接，则响应也使用长连接
        if(HttpUtil.isKeepAlive(request)){
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        //将响应写入到Channel
        context.writeAndFlush(response);
    }
}
