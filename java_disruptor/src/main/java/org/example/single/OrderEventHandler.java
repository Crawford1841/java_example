package org.example.single;

import com.lmax.disruptor.EventHandler;
import org.example.entity.OrderEvent;

//消费者
public class OrderEventHandler implements EventHandler<OrderEvent> {

    @Override
    public void onEvent(OrderEvent orderEvent, long l, boolean b) throws Exception {
        //取出订单对象的价格
        System.out.println("消费者："+orderEvent.getValue());
    }
}
