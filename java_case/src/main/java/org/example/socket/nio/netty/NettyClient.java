package org.example.socket.nio.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        //1、创建一个线程组
        EventLoopGroup group = new NioEventLoopGroup();
        //2、创建客户端的启动助手，完成相关配置
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)//3、设置线程组
                .channel(NioSocketChannel.class)//4、设置客户端通道实现类
                .handler(new ChannelInitializer<SocketChannel>() {//5、创建一个通道初始化对象
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //6、往Pipeline链中添加自定义的handler
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
        System.out.println("........客户端 准备就绪 msg发射");

        //7、启动客户端去连接服务端 connect方法是异步的 sync方法是同步阻塞的
        ChannelFuture cf = bootstrap.connect("127.0.0.1", 9999).sync();
        //8、关闭连接(异步非阻塞)
        cf.channel().closeFuture().sync();


    }
}
