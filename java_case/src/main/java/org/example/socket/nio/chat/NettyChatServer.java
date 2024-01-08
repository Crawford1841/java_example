package org.example.socket.nio.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 聊天程序服务端netty版本
 */
public class NettyChatServer {
    private int port;//服务端端口号

    public NettyChatServer(int port) {
        this.port = port;
    }

    public void run(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    //往pipeline中添加一个解码器
                    pipeline.addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
                    //往pipeline链中添加一个编码器
                    pipeline.addLast("encoder",new StringEncoder(CharsetUtil.UTF_8));
                    //往pipeline链中添加自定义的handler（业务处理类）
                    pipeline.addLast(new NettyChatServerHandler());
                }
            });
            System.out.println("基于Netty的网络真人聊天室 Server 启动..........");
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("基于Netty的网络真人聊天室 Server 关闭.........");
        }
    }

    public static void main(String[] args) {
        new NettyChatServer(9999).run();
    }

}
