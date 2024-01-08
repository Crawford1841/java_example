package org.example.collection;

import java.sql.Time;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 阻塞队列的一端是给生产者放数据用，另一端给消费者拿数据用。阻塞队列是线程安全的，所以生
 * 产者和消费者都可以是多线程的。
 * take()方法获取并移除队列的头结点，一旦执行take时，队列里无数据则阻塞，直到队列里有数
 * 据。
 * put()方法是插入元素，但是如何队列已满，则无法继续插入，则阻塞，直到队列中有空闲空间。
 * 是否有界（容量多大），这是非常重要的属性，无界队列Integer.MAX_VALUE，认为是无限容量。
 */
public class ArrayBlockingQueueCase {
    /**
     * 有界，可以指定容量
     * 公平：可以指定是否需要保证公平，如果想要保证公平，则等待最长时间的线程会被优先处理，不过会带来一定的性能损耗。
     * 场景：有10个面试者，只有1个面试官，大厅有3个位子让面试者休息，每个人面试时间10秒，模拟所有人面试的场景。
     * 1. ArrayBlockingQueue ：基于数组实现的有界阻塞队列
     * 2. LinkedBlockingQueue ：基于链表实现的有界阻塞队列
     * 3. SynchronousQueue：不存储元素的阻塞队列
     * 4. PriorityBlockingQueue ：支持按优先级排序的无界阻塞队列
     * 5. DelayQueue：优先级队列实现的无界阻塞队列
     * 6. LinkedTransferQueue：基于链表实现的无界阻塞队列
     * 7. LinkedBlockingDeque：基于链表实现的双向无界阻塞队列
     */
    static ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(3);
    public static void main(String[] args) {
        Interviewer r1 = new Interviewer(queue);//面试官
        Engineers e2 = new Engineers(queue);//程序员们
        new Thread(r1).start();
        new Thread(e2).start();
    }
}

class Interviewer implements Runnable{
    BlockingQueue<String> queue;
    public Interviewer(BlockingQueue<String> queue){
        this.queue = queue;
    }
    @Override
    public void run() {
        System.out.println("面试官：我准备好了，可以开始面试了");
        String msg;
        try {
            while (!(msg=queue.take()).equals("stop")){
                System.out.println(msg+" 面试开始.....");
                TimeUnit.SECONDS.sleep(5);
                System.out.println(msg+"  面试结束....");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Engineers implements Runnable{
    BlockingQueue<String> queue;
    public Engineers(BlockingQueue<String> queue){
        this.queue = queue;
    }
    @Override
    public void run() {
        for(int i=0;i<=10;i++){
            String candidate = "程序员"+i;
            try {
                queue.put(candidate);
                System.out.println(candidate+"  就坐=等待面试");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            queue.put("stop");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}