package org.example.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Map;
import java.util.Optional;
import org.example.http.DefaultHeroServlet;
import org.example.http.HttpCustomRequest;
import org.example.http.HttpCustomResponse;
import org.example.servlet.CustomRequest;
import org.example.servlet.CustomResponse;
import org.example.servlet.Servlet;
/**
 * HeroCat服务端处理器
 *
 * 1）从用户请求URI中解析出要访问的Servlet名称
 * 2）从nameToServletMap中查找是否存在该名称的key。若存在，则直接使用该实例，否则执
 行第3）步
 * 3）从nameToClassNameMap中查找是否存在该名称的key，若存在，则获取到其对应的全限定
 性类名，
 * 使用反射机制创建相应的serlet实例，并写入到nameToServletMap中，若不存在，则直
 接访问默认Servlet
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Map<String,Servlet> nameTosServeltMap;
    private Map<String,String> nameToClassNameMap;
    public ServerHandler(Map<String, Servlet> nameToservletMap, Map<String, String> nameToClassNameMap) {
        this.nameToClassNameMap = nameToClassNameMap;
        this.nameTosServeltMap = nameToservletMap;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){
            HttpRequest request = (HttpRequest) msg;
            String uri = request.uri();
            //从请求中解析出要访问的Servlet名称
            String servletName = uri.substring(uri.lastIndexOf("/")+1,uri.indexOf("?")).toLowerCase();
            Servlet servlet = new DefaultHeroServlet();
            if(nameTosServeltMap.containsKey(servletName)){
                servlet = nameTosServeltMap.get(servletName);
            }else if(nameToClassNameMap.containsKey(servletName)){
                //double-check，双重检测锁
                if(nameTosServeltMap.get(servletName) == null){
                    synchronized (this){
                        if(nameTosServeltMap.get(servletName) == null){
                            //获取当前Servlet的全限定类名
                            String className = nameToClassNameMap.get(servletName);
                            //使用反射机制创建Servlet实例
                            servlet = (Servlet) Class.forName(className).getDeclaredConstructor().newInstance();
                            //将servlet实例写入到nameToServletMap
                            nameTosServeltMap.put(servletName,servlet);
                        }
                    }
                }
            }
            //代码走到这肯定不为空
            CustomRequest req = new HttpCustomRequest(request);
            CustomResponse res = new HttpCustomResponse(request,ctx);
            //根据不同的请求类型，调用Servlet实例的不同方法
            if(request.method().name().equalsIgnoreCase("GET")){
                servlet.doGet(req,res);
            }else if(request.method().name().equalsIgnoreCase("POST")){
                servlet.doPost(req,res);
            }
            ctx.close();
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
