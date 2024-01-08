package org.example.socket.nio.encoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import org.example.socket.nio.netty.NettyServerHandler;

public class NettyEncoderDecoderServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup pgroup = new NioEventLoopGroup();//线程组，用来处理网络事件处理（接受客户端连接）
        EventLoopGroup cgroup = new NioEventLoopGroup();//线程组，用来处理网络事件处理（接受客户端连接）
        ServerBootstrap b = new ServerBootstrap();
        b.group(pgroup,cgroup).channel(NioServerSocketChannel.class)//注册服务端channel
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("decoder",new ProtobufDecoder(BookMessage.Book.getDefaultInstance()));
                        socketChannel.pipeline().addLast(new NettyEncoderDecoderServerHandler());
                    }
                });
        ChannelFuture cf = b.bind(9998).sync();
        System.out.println(".........Server is Starting");
        cf.channel().closeFuture().sync();
        pgroup.shutdownGracefully();
        cgroup.shutdownGracefully();

    }
}
