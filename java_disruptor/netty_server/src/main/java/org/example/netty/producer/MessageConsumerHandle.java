package org.example.netty.producer;

import io.netty.channel.ChannelHandlerContext;
import org.example.consumer.MessageConsumer;
import org.example.entity.TranslatorData;
import org.example.entity.TranslatorDataWapper;

public class MessageConsumerHandle extends MessageConsumer {
    public MessageConsumerHandle(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWapper event) throws Exception {
        TranslatorData request = event.getData();
        ChannelHandlerContext ctx = event.getCtx();
        //1、业务逻辑处理：
        System.err.println("Server端："+request.toString());
        //2、回送响应消息：
        TranslatorData response = new TranslatorData();
        response.setId("resp："+request.getId());
        response.setName("resp："+request.getName());
        response.setMessage("resp："+request.getMessage());
        //写出response响应信息
        ctx.writeAndFlush(response);
    }
}
