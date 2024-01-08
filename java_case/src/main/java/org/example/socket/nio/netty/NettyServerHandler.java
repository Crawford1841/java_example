package org.example.socket.nio.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;


//服务端的业务处理类
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    //读取数据事件，msg就客户端发过来的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        System.out.println("Server："+ctx);
        //用缓冲区接受数据
        ByteBuf buffer = (ByteBuf) msg;
        //转换成字符串
        System.out.println("client msg："+buffer.toString(CharsetUtil.UTF_8));
    }

    //数据读取完毕事件，读取完客户端数据后回复客户端
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        //Unpooled.copiedBuffer获取到缓冲区
        //第一个参数是向客户端传的字符串
        ctx.writeAndFlush(Unpooled.copiedBuffer("学习Netty",CharsetUtil.UTF_8));
    }
    //异常发生事件
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常时关闭ctx，ctx是相关信息的汇总，关闭它其它的也就关闭了。
        ctx.close();
    }
}
