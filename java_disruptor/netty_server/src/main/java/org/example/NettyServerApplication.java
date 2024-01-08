package org.example;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import org.example.consumer.MessageConsumer;
import org.example.factory.RingBufferWorkerPoolFactory;
import org.example.netty.producer.MessageConsumerHandle;
import org.example.netty.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyServerApplication {

    //public static void main(String[] args)throws Exception {
    //    SpringApplication.run(NettyServerApplication.class,args);
    //    new NettyServer();
    //}

    public static void main(String[] args)throws Exception {
        SpringApplication.run(NettyServerApplication.class,args);
        MessageConsumer[] consumers = new MessageConsumer[4];

        for(int i=0;i< consumers.length;i++){
            MessageConsumer messageConsumer = new MessageConsumerHandle("code:serverId:"+i);
            consumers[i] = messageConsumer;
        }

        RingBufferWorkerPoolFactory.getInstance().initAndStart(ProducerType.MULTI,1024*1024,new BlockingWaitStrategy(),consumers);
        new NettyServer();
    }
}
