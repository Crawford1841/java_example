package org.example.socket.nio.encoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.example.socket.nio.netty.NettyClientHandler;

public class NettyEncoderDecoderClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast("encoder",new ProtobufEncoder());
                socketChannel.pipeline().addLast(new NettyEncoderDecoderClientHandler());
            }
        });
        // 启动客户端
        ChannelFuture cf = bootstrap.connect("127.0.0.1", 9998).sync();
        // 5等待连接关闭
        cf.channel().closeFuture().sync();
    }
}
