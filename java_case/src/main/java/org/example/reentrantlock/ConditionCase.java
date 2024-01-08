package org.example.reentrantlock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 当线程1需要等待某个条件时就去执行condition.await()方法，一旦执行await()方法，线程就会进入阻塞
 * 状态。通常会有另一个线程2去执行对应条件，直到这个条件达成时，线程2就会执行condition.signal()
 * 方法，此时JVM就会从被阻塞的线程里找到那些等待该condition的线程，当线程1收到可执行信号时，
 * 它的线程状态就会变成Runnable可执行状态。
 * 常用方法：
 * signalAll()会唤起所有正在等待的线程。
 * signal()是公平的，只会唤起那个等待时间最长的线程。
 * 注意点：
 * Condition用来代替Object.wait/notify两者用法一样
 * Condition的await()会自动释放持有的Lock锁这点也和Object.wait一样
 * 调用await时必须持有锁，否则会抛出异常。
 */
public class ConditionCase {
    /**
     * 案例：Tony仨小哥洗剪吹
     * 演示多线程之间按顺序调用，实现A->B->C
     * 三个线程Tony要求如下：
     * tony雄雄-洗头，tony超超-理发，tony麦麦-吹干
     * 。。。
     * tony雄雄-洗头，tony超超-理发，tony麦麦-吹干
     * 依次来10轮
     */
    public static void main(String[] args) {
        ShareData shareData = new ShareData();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                shareData.wash();
            }
        }, "tony-熊熊").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                shareData.cut();
            }
        }, "tony-超超").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                shareData.cook();
            }
        }, "tony-麦麦").start();
    }

    static class ShareData {
        private volatile int number = 1;///tony-雄雄:1, tony-超超:2, tony-麦麦:3
        private Lock lock = new ReentrantLock();
        private Condition c1 = lock.newCondition();
        private Condition c2 = lock.newCondition();
        private Condition c3 = lock.newCondition();

        /**
         * A线程每一轮要执行的操作
         */
        public void wash() {
            lock.lock();
            try {
                //判断
                while (number != 1) {
                    c1.await();
                }
                //模拟线程执行任务
                System.out.println(Thread.currentThread().getName() + "-洗头");
                //通知
                number = 2;
                c2.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        /**
         * B线程每一轮要执行的操作
         */
        public void cut() {
            lock.lock();
            try {
                //判断
                while (number != 2) {
                    c2.await();
                }
                //模拟线程执行的任务
                System.out.println(Thread.currentThread().getName() + "-理发");
                //通知
                number = 3;
                c3.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void cook() {
            lock.lock();
            try {
                //判断
                while (number != 3) {
                    c3.await();
                }
                //模拟线程执行的任务
                System.out.println(Thread.currentThread().getName() + "-吹干");
                //通知
                number = 1;
                c1.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
