package org.example.reentrantlock;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 信号量的作用就是维护一个”许可证”的计数，线程可以”获取”许可证，那信号量剩余的许可证就减少一
 * 个，线程也可以”释放”一个许可证，那信号量剩余的许可证就可以加一个。当信号量拥有的许可证数为0
 * 时，下一个还要要获取许可证的线程就需要等待，直到有另外的线程释放了许可证
 *
 * 主要方法：
 *      构造函数：Semaphore(int permits,Boolean fair)：可以设置是否使用公平策略，如果传入true,则Semaphore会把之前等待的线程放到FIFO队列里，以便有了新许可证可以分给之前等待时间最长的线程。
 *       acquire()：获取许可证，当一个线程调用acquire操作时，他要么通过成功获取信号量（信号量减1），要么一直等待下去，直到有线程释放信号量，或超时。
 *       release()：释放许可证，会将信号量加1，然后唤醒等待的线程。
 */
public class SemaphoreCase {
    /**
     * Semaphore案例：三辆小汽车抢车位
     * Semaphore信号量主要作用：1.用于多个共享资源的互斥使用，2.用于并发线程数的控制
     */

    public static void main(String[] args) {
        //模拟资源类，有3个空车位
        Semaphore semaphore = new Semaphore(3);
        for(int i=1;i<=6;i++){
            new Thread(()->{
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName()+"\t抢到车位");
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(Thread.currentThread().getName()+"\t停车3s后离开车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    //释放资源
                    semaphore.release();
                }
            },"Thread-Car"+String.valueOf(i)).start();
        }
    }
}
