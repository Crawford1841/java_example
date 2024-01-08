package org.example.netty.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.example.consumer.MessageConsumer;
import org.example.entity.TranslatorData;
import org.example.entity.TranslatorDataWapper;

public class MessageProducerHandle extends MessageConsumer {

    public MessageProducerHandle(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWapper event) throws Exception {
        TranslatorData response = event.getData();
        ChannelHandlerContext ctx = event.getCtx();
        //业务逻辑处理：
        try {
            System.out.println("Clinet端："+response.toString());
        }finally {
            ReferenceCountUtil.release(response);
        }

    }
}
