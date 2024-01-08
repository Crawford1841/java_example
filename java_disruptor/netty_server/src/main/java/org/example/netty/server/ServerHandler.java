package org.example.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.entity.TranslatorData;
import org.example.factory.RingBufferWorkerPoolFactory;
import org.example.producer.MessageProducer;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    //无disruptor
    //@Override
    //public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //    TranslatorData request = (TranslatorData) msg;
    //    System.out.println("Server端："+request.toString());
    //    //数据库持久化操作 IO读写——》交给一个线程池，去异步调用执行
    //    TranslatorData response = new TranslatorData();
    //    response.setId("resp："+request.getId());
    //    response.setName("resp："+request.getName());
    //    response.setMessage("resp："+request.getMessage());
    //    //写出response响应信息：
    //    ctx.writeAndFlush(response);
    //
    //}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TranslatorData request = (TranslatorData) msg;
        //自己的服务应用应该有一个ID生成规则
        String producerId = "code:seesionId:001";
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        messageProducer.onData(request,ctx);

    }
}
