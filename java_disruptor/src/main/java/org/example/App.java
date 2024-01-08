package org.example;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sun.org.apache.xpath.internal.operations.Or;
import java.nio.ByteBuffer;
import java.sql.Time;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.example.entity.Order;
import org.example.entity.OrderEvent;
import org.example.mutiple.ConsumerHandler;
import org.example.mutiple.EventExceptionHandler;
import org.example.mutiple.Producer;
import org.example.single.OrderEventFactory;
import org.example.single.OrderEventHandler;
import org.example.single.OrderEventProducer;
import org.w3c.dom.events.EventException;

/**
 * 1. 创建工厂类，用于生产Event对象
 * 2. 创建Consumer监听类，用于监听，并处理Event
 * 3. 创建Disruptor对象，并初始化一系列参数：工厂类、RingBuffer大小、线程池、单生产者或多生产者、Event等待策略
 * 4. 编写Producer组件，向Disruptor容器中去投递Event
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        //单生产者消费
        //OrderEventFactory orderEventFactory = new OrderEventFactory();
        //int ringBufferSize = 8;
        //ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ///**
        // * 1、eventFactory：消息工厂对象
        // * 2、ringBufferSize：容器长度
        // * 3、executor：线程池，建议使用自定义的线程池，线程上限
        // * 4、ProducerType：单生产者或多生产者
        // * 5、waitStrategy：等待策略
        // */
        ////1、实例化disrupotor对象
        //Disruptor<OrderEvent> disruptor = new Disruptor<OrderEvent>(orderEventFactory,ringBufferSize,executor,
        //        ProducerType.SINGLE,new BlockingWaitStrategy());
        ////2、添加消费者的监听(去构建disruptor与消费者的一个关联关系)
        //disruptor.handleEventsWith(new OrderEventHandler());
        ////3、启动disruptor
        //disruptor.start();
        ////4、取到容器后通过生产者去生产消息
        ////获取实际存储数据的容器RingBuffer
        //RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
        ////生产者
        //OrderEventProducer producer = new OrderEventProducer(ringBuffer);
        ////先初始化ByteBuffer长度为8个字节
        //ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        ////生产100个orderEvent->value->i 0-99
        //for(long i=0;i<100;i++){
        //    byteBuffer.putLong(0,i);
        //    producer.sendData(byteBuffer);
        //}
        //disruptor.shutdown();
        //executor.shutdown();

        /**
         * 多实例消费者
         */
        //1、创建RingBuffer，Disruptor包含RingBuffer
        RingBuffer<Order> mutil_ringBuffer = RingBuffer.create(
                ProducerType.MULTI,//多生产者
                new EventFactory<Order>() {
                    @Override
                    public Order newInstance() {
                        return new Order();
                    }
                },
                1024*1024,
                new YieldingWaitStrategy()
        );
        //2、创建ringBuffer屏障
        SequenceBarrier sequenceBarrier = mutil_ringBuffer.newBarrier();
        //3、创建多个消费者数组
        ConsumerHandler[] consumers = new ConsumerHandler[10];
        for(int i=0;i<consumers.length;i++){
            consumers[i] = new ConsumerHandler("C"+i);
        }
        //4、构建多消费者工作池
        WorkerPool<Order> workerPool = new WorkerPool<Order>(mutil_ringBuffer,sequenceBarrier,new EventExceptionHandler(),consumers);

        //5、设置多个消费者的sequence序号，用于单独统计消费者的消费进度。消费进度让RingBuffer知道
        mutil_ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        //6、启动workPool
        workerPool.start(Executors.newFixedThreadPool(5));//在实际开发，自定义线程池
        //要生产100个生产者，每个生产者发送100个数据，总共投递10000
        final CountDownLatch latch = new CountDownLatch(1);
        //设置100个生产者向ringbuffer中去投递数据
        for(int i=0;i<100;i++){
            final Producer producer_mutil = new Producer(mutil_ringBuffer);
            new Thread(()->{
                try {
                    //每次一个生产者创建后就处理等待。先创建100个生产者，创建完100个生产者后再去发送数据
                    latch.await();
                }catch (Exception e){
                    e.printStackTrace();
                }
                for(int j=0;j<100;j++){
                    producer_mutil.sendData(UUID.randomUUID().toString());
                }
            }).start();
        }
        //把所有线程创建完
        TimeUnit.SECONDS.sleep(2);
        //唤醒
        latch.countDown();
        //休眠10s，让生产者把100次循环走完
        TimeUnit.SECONDS.sleep(10);
        System.out.println("任务总数："+consumers[0].getCount());

    }
}
