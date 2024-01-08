package org.example.entity;

import io.netty.channel.ChannelHandlerContext;
import java.io.Serializable;

public class TranslatorDataWapper implements Serializable {
    private TranslatorData data;
    private ChannelHandlerContext ctx;

    public TranslatorData getData() {
        return data;
    }

    public TranslatorDataWapper setData(TranslatorData data) {
        this.data = data;
        return this;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public TranslatorDataWapper setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        return this;
    }
}
