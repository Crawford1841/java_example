package org.example.reentrantlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2023/12/23 21:06
 * 用ReentrantLock解决可见性的问题
 */
public class RentrantLockCase {
    public static void main(String[] args) throws InterruptedException {
        VolatileDemo volatileDemo = new VolatileDemo();
        for(int i=0;i<2;i++){
            Thread thread = new Thread(volatileDemo);
            thread.start();
        }
        TimeUnit.SECONDS.sleep(1);
        System.out.println("count="+volatileDemo.count);
    }


    static class VolatileDemo implements Runnable{
        public int count = 0;
        public Lock lock = new ReentrantLock();
        @Override
        public void run() {
            addCount();
        }
        public void addCount(){
            lock.lock();
            for(int i=0;i<10000;i++){
                count++;
            }
            lock.unlock();
        }
    }
}
