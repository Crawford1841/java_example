package org.example.single;

import com.lmax.disruptor.RingBuffer;
import java.nio.ByteBuffer;
import org.example.entity.OrderEvent;

public class OrderEventProducer {

    //ringBuffer存储数据的一个容器
    private RingBuffer<OrderEvent> ringBuffer;
    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    //生产者投递的数据
    public void sendData(ByteBuffer data){
        //1、在生产者发送消息时，首先要从ringBuffer中找到一个可用的序号
        long sequnce = ringBuffer.next();
        try {
            //2.根据这个序号找到具体的OrderEvent元素, 此时获取到的OrderEvent对象是一个没有被赋值的空对象。value
            OrderEvent orderEvent = ringBuffer.get(sequnce);
            orderEvent.setValue(data.getLong(0));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //4、提交发布操作，生产者最后要发布消息
            ringBuffer.publish(sequnce);
        }
    }
}
