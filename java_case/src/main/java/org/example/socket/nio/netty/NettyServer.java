package org.example.socket.nio.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //1、创建一个线程组，接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //2、创建一个线程组：处理网络操作
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //3、创建服务端启动助手来配置参数
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workerGroup)//4、设置两个线程组
                .channel(NioServerSocketChannel.class)//5、使用NioServerSocketChannel作为服务端通道的实现
                .option(ChannelOption.SO_BACKLOG,128)//6、设置线程队列中的等待连接的个数
                .childOption(ChannelOption.SO_KEEPALIVE,true)//7、保持活动连接状态
                .childHandler(new ChannelInitializer<SocketChannel>() {//8、创建一个通道初始化对象
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //9、王pipeline链中添加自定义的handler类
                        socketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                });
        System.out.println("...........服务端启动中 init port:9999");
        ChannelFuture cf = b.bind(9999).sync();//10. 绑定端口 bind方法是异步的sync方法是同步阻塞的
        System.out.println("............服务端   启动成功..........");
        //11、关闭通道，关闭线程组
        cf.channel().closeFuture().sync();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
