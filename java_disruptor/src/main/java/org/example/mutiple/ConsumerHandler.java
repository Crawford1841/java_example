package org.example.mutiple;

import com.lmax.disruptor.WorkHandler;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.entity.Order;

public class ConsumerHandler implements WorkHandler<Order> {
    //每个消费者都有自己的id
    private String comsumerId;
    //计数统计，多个消费者，所有的消费者总共消费了多个消息
    private static AtomicInteger count = new AtomicInteger(0);
    private Random random = new Random();

    public ConsumerHandler(String comsumerId){
        this.comsumerId = comsumerId;
    }

    @Override
    public void onEvent(Order event) throws Exception {
        //模拟消费者处理消息的耗时
        TimeUnit.MILLISECONDS.sleep(1* random.nextInt(5));
        System.out.println("当前消费者："+this.comsumerId+"，消费信息ID："+event.getId());
        //count计数器+1，表示消费了一个消息
        count.incrementAndGet();
    }
    //返回所有消费者总共消费的消息个数
    public int getCount(){
        return count.get();
    }
}
