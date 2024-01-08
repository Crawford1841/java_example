package org.example;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.example.consumer.MessageConsumer;
import org.example.factory.RingBufferWorkerPoolFactory;
import org.example.netty.client.NettyClinet;
import org.example.netty.consumer.MessageProducerHandle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyClientApplication {

    //public static void main(String[] args)throws Exception {
    //    SpringApplication.run(NettyClientApplication.class,args);
    //    //建立连接发送消息
    //    new NettyClinet().sendData();
    //}


    public static void main(String[] args)throws Exception {
        SpringApplication.run(NettyClientApplication.class,args);
        MessageConsumer[] consumers = new MessageConsumer[4];
        for(int i=0;i<consumers.length;i++){
            MessageConsumer messageConsumer = new MessageProducerHandle("code:clientId:"+i);
            consumers[i] = messageConsumer;
        }
        RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
                1024*1024,
                //new YieldingWaitStrategy(),
                new BlockingWaitStrategy(),
                consumers);
        //建立连接发送消息
        new NettyClinet().sendData();
    }
}
