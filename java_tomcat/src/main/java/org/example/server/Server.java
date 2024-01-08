package org.example.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.example.servlet.Servlet;
import org.example.util.DocumentNodeUtils;

/**
 * tomcat功能实现
 */
public class Server {
    //key为Servlet的简单类名，value为对应Servlet实例
    private Map<String, Servlet> nameToservletMap = new ConcurrentHashMap<>();
    //key为Servlet的简单类名，value为对应Servlet类的全限定类名
    private Map<String,String> nameToClassNameMap = new ConcurrentHashMap<>();
    private String basePackage;
    public Server(String basePackage){
        this.basePackage = basePackage;
    }

    //启动tomcat
    public void start() throws Exception {
        //加载指定包中的所有Servlet的类名
        cacheClassName(basePackage);
        //启动server服务
        runServer();
    }


    private void cacheClassName(String basePackage) {
        URL resouce = this.getClass().getClassLoader().getResource(basePackage.replaceAll("\\.","/"));
        //若目录没有资源则直接结束
        if(resouce == null){
            return;
        }
        //将URL资源转换为File资源
        File dir = new File(resouce.getFile());
        //遍历指定包及子孙包中的所有文件，查找所有.class文件
        for (File file:dir.listFiles()){
            if(file.isDirectory()){
                //若当前遍历的file为目录，则递归调用当前方法
                cacheClassName(basePackage+"."+file.getClass());
            }else if(file.getName().endsWith(".class")){
                String simpleClassName = file.getName().replace(".class","").trim();
                //key为简单的类名，value为全限定类名  （是否可以参考Spring的三级缓存机制？）
                nameToClassNameMap.put(simpleClassName.toLowerCase(),basePackage+"."+simpleClassName);
            }
        }
    }

    private void runServer()throws Exception {
        EventLoopGroup parent = new NioEventLoopGroup();
        EventLoopGroup child = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parent,child)
                    //指定存放请求的队列长度
                    .option(ChannelOption.SO_BACKLOG,1024)
                    //指定是否用心跳检测机制来检测长连接的存活性，即客户端的存活性
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new ServerHandler(nameToservletMap,nameToClassNameMap));
                        }
                    });
            int port = initPort();
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("tomcat启动成功，监听端口号为："+port);
            future.channel().closeFuture().sync();
        }finally {
            parent.shutdownGracefully();
            child.shutdownGracefully();
        }
    }

    private int initPort()throws Exception {
        //初始化端口，读取配置文件server.xml中的端口号
        int port =(int) DocumentNodeUtils.readNode("//port");
        return port;
    }
}
