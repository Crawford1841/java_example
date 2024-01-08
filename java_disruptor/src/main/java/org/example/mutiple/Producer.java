package org.example.mutiple;

import com.lmax.disruptor.RingBuffer;
import org.example.entity.Order;

public class Producer {
    private RingBuffer<Order> ringBuffer;

    //为生产者绑定ringBuffer
    public Producer(RingBuffer<Order> ringBuffer){
        this.ringBuffer = ringBuffer;
    }

    //发送数据
    public void sendData(String uuid){
        //1、获取到可用的sequece
        long sequece = ringBuffer.next();
        try {
            Order order = ringBuffer.get(sequece);
            order.setId(uuid);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //发布序号
            ringBuffer.publish(sequece);
        }
    }
}
