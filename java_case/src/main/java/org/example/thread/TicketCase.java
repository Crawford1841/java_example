package org.example.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 买票案例
 */
public class TicketCase {
    public static void main(String[] args) {
        SellTicketTask task = new SellTicketTask();
        Thread t1 = new Thread(task, "窗口1");
        Thread t2 = new Thread(task, "窗口2");
        Thread t3 = new Thread(task, "窗口3");
        t1.start();
        t2.start();
        t3.start();
    }
}

class SellTicketTask implements Runnable {
    private Integer tickets = 100;
    private final Object lock = new Object();//锁对象，可以是任意类型数据

    private Lock reentrantlock = new ReentrantLock();

    /*
     * 每个窗口执行相同的卖票操作
     * 窗口永远开启，所有窗口卖完100张票为止
     */
    @Override
    public void run() {
        while (true) {
            reentrantlock.lock();
            if (tickets > 0) {
                //模拟出票时间
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String name = Thread.currentThread().getName();
                System.out.println(name + "-正在卖：" + tickets--);
            }else{
                reentrantlock.unlock();
            }
        }
//        while (true){
//            synchronized (lock){
//                if(tickets>0){
//                    //模拟出票时间
//                    try {
//                        Thread.sleep(20);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    String name = Thread.currentThread().getName();
//                    System.out.println(name+"-正在卖："+tickets--);
//
//                }
//            }
//        }
    }
}