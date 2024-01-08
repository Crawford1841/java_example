package org.example.consumer;

import com.lmax.disruptor.WorkHandler;
import org.example.entity.TranslatorDataWapper;

public abstract class MessageConsumer implements WorkHandler<TranslatorDataWapper> {
    protected String consumerId;

    public MessageConsumer(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public MessageConsumer setConsumerId(String consumerId) {
        this.consumerId = consumerId;
        return this;
    }
}
