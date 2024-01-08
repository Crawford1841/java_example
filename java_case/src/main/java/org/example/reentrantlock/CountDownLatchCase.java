package org.example.reentrantlock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 作用：线程处于等待状态，指导计数减为0，等待线程才继续执行
 * 主要方法：
 * 构造函数：CountDownLatch(int count)：只有一个构造函数，参数count为需要倒数的数值。
 * await()：当一个或多个线程调用await()时，这些线程会阻塞。
 * countDown()：其他线程调用countDown()会将计数器减1，调用countDown方法的线程不会阻塞。当计数器的值变为0时，因await方法阻塞的线程会被唤醒，继续执行
 */
public class CountDownLatchCase {
    /**
     * CountDownLatch案例：6个程序猿加班
     * 当计数器的值变为0时，因await方法阻塞的线程会被唤醒，继续执行
     */
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        new Thread(() -> {
            try {
                countDownLatch.await();//卷王也是有极限的，设置超时时间
                System.out.println(Thread.currentThread().getName() + "\t卷王最后关灯走人");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "7").start();

        for (int i = 0; i <= 6; i++) {
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t上完班，离开公司");
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }
    }
}
