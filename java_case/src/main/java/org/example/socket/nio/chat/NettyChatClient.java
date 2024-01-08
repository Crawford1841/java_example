package org.example.socket.nio.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.Scanner;

//聊天程序客户端
public class NettyChatClient {
    private final String host; //服务端IP地址
    private final int port; //服务端端口号
    public NettyChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void run(){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    //往pipeline链中添加一个解码器
                    pipeline.addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
                    //往pipeline链中添加一个编码器
                    pipeline.addLast("encoder",new StringEncoder(CharsetUtil.UTF_8));
                    //往pipeline链中添加自定义的handler 业务处理类
                    pipeline.addLast(new NettyChatChatClientHandler());
                }
            });

            ChannelFuture cf = bootstrap.connect(host,port).sync();
            Channel channel = cf.channel();
            System.out.println("------ "+channel.localAddress().toString().substring(1)+"------");
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String msg = scanner.nextLine();
                channel.writeAndFlush(msg+"\r\n");
            }
            cf.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyChatClient("192.168.0.46",9999).run();
    }
}
