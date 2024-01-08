package org.example.single;

import com.lmax.disruptor.EventFactory;
import org.example.entity.OrderEvent;

//建立一个工厂类，用于创建Event的实例（OrderEvent)
public class OrderEventFactory implements EventFactory<OrderEvent> {

    @Override
    public OrderEvent newInstance() {
        //返回空的数据对象，不是null，OrderEvent，value属性还没有赋值
        return new OrderEvent();
    }
}
