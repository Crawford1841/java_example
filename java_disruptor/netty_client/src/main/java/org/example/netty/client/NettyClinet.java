package org.example.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.example.entity.TranslatorData;
import org.example.factory.MarshallingCodeCFactory;

public class NettyClinet {

    public static final String HOST = "127.0.0.1";

    public static final int PORT = 8765;

    //扩展 完善 池化 ConcurrentHashMap<Key --- String,value --->Channel>
    private Channel channel;

    //1、创建工作线程组：用于实际处理业务的线程组
    private EventLoopGroup workGroup = new NioEventLoopGroup();

    private ChannelFuture cf;

    public NettyClinet() {
        this.connect(HOST, PORT);
    }
    private void connect(String host, int port) {
        //2、辅助类 注意Client和Server是不一样的
        Bootstrap bootstrap = new Bootstrap();
        try {
            //绑定线程组
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    //表示缓冲区动态调配 自适应
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    //缓存区 池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //网络传递对象，客户端和服务端都要做编码和解码操作
                            ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            //绑定端口，同步等待请求连接
            this.cf = bootstrap.connect(host, port).sync();
            System.err.println("Client connected.....");
            //进行数据发送，首先获取channel
            this.channel = cf.channel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //发送数据的方法，提供给外部使用
    public void sendData() {
        for (int i = 0; i < 6000000; i++) {
            TranslatorData request = new TranslatorData();
            request.setId(String.valueOf(i));
            request.setName("请求消息名称 "+i);
            request.setMessage("请求消息内容 "+i);
            this.channel.writeAndFlush(request);
        }
    }

    public void close()throws Exception{
        cf.channel().closeFuture().sync();
        workGroup.shutdownGracefully();
        System.out.println("Server Shtudown.....");
    }

}
