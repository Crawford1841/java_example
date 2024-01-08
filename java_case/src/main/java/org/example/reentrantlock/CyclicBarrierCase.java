package org.example.reentrantlock;

import java.util.concurrent.CyclicBarrier;

/**
 * 当有大量线程互相配合，分别计算不同任务，并且需要最后统一汇总时，就可以用CyclicBarrier，它可
 * 以构造一个集结点，当某一个线程执行完，它就会到集结点等待，直到所有线程都到集结点，则该栅栏
 * 就被撤销，所有线程统一出再，继续执行剩下的任务
 * <p>
 * 主要方法：
 * 构造函数：CyclicBarrier(int parties, Runnable barrierAction)，设置聚集的线程数量和集齐线程数的结果之后要执行的动作。
 * await()：阻塞当前线程，待凑齐线程数量之后继续执行
 */
public class CyclicBarrierCase {
    /**
     * 集齐七龙珠召唤神龙
     */
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> {
            System.out.println("召唤神龙");
        });
        for (int i = 1; i <= 7; i++) {
            final int tempInt = i;
            new Thread(()->{
                try {
                    System.out.println(Thread.currentThread().getName()+"\t收集到第"+tempInt+"颗龙珠");
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName()+"\t第"+tempInt+"颗龙珠飞走了");
                }catch (Exception e){
                    e.printStackTrace();
                }
            },"Thread-"+String.valueOf(tempInt)).start();
        }

    }
}
