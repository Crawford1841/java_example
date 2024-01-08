package org.example.volatil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 解决volatile的原子性bug
 */
public class Atomic {

    public static void main(String[] args) {
        VolatileDemo volatileDemo = new VolatileDemo();
        for(int i=0;i<2;i++){
            Thread thread1 = new Thread(volatileDemo);
            thread1.start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("count="+volatileDemo.integer);
    }


    static class VolatileDemo implements Runnable{
        public AtomicInteger integer = new AtomicInteger();
        public void run() {
            addCount();
        }
        public void  addCount(){
            for(int i=0;i<1000;i++){
                integer.incrementAndGet();
            }
        }
    }
}
