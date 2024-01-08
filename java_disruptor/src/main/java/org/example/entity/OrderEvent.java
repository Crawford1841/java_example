package org.example.entity;

/**
 * 订单对象，生产者要生产订单对象，消费者消费订单对象
 */
public class OrderEvent {
    private long value;//订单价格

    public long getValue() {
        return value;
    }

    public OrderEvent setValue(long value) {
        this.value = value;
        return this;
    }
}
