package org.example.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.example.entity.TranslatorData;
import org.example.factory.RingBufferWorkerPoolFactory;
import org.example.producer.MessageProducer;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    //@Override
    //public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    //
    //        try {
    //            TranslatorData response = (TranslatorData) msg;
    //            System.out.println("Client端："+response.toString());
    //        }finally {
    //            //一定要注意，用完了缓存要进行释放
    //            ReferenceCountUtil.release(msg);
    //        }
    //
    //}
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            TranslatorData response = (TranslatorData) msg;
            String producerId = "code:sessionId:002";
            MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
            messageProducer.onData(response,ctx);
    }
}
