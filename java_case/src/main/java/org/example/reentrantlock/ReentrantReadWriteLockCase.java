package org.example.reentrantlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 可重入锁ReentrantLock是互斥锁，互斥锁在同一时刻仅有一个线程可以进行访问，但是在大多数场景
 * 下，大部分时间都是提供读服务，而写服务占有的时间较少。然而读服务不存在数据竞争问题，如果一
 * 个线程在读时禁止其他线程读势必会导致性能降低，所以就出现了读写锁。
 * 读写锁的主要特性：
 * 公平性：支持公平性和非公平性。
 * 重入性：支持重入。读写锁最多支持65535个递归写入锁和65535个递归读取锁。
 * 锁降级：写锁能够降级成为读锁，但读锁不能升级为写锁。遵循获取写锁、获取读锁在释放写锁的次序
 */
public class ReentrantReadWriteLockCase {
    private static volatile int count = 0;

    public static void main(String[] args) throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        WriteLockDemo writeLockDemo = new WriteLockDemo(lock);
        ReadLockDemo readLockDemo = new ReadLockDemo(lock);

        for(int i=0;i<3;i++){
            new Thread(writeLockDemo,"写线程"+i).start();
        }
        for (int i=0;i<5;i++){
            new Thread(readLockDemo,"读线程"+i).start();
        }
    }


    static class WriteLockDemo implements Runnable {
        ReentrantReadWriteLock lock;
        public WriteLockDemo(ReentrantReadWriteLock lock){
            this.lock = lock;
        }
        @Override
        public void run() {
            for(int i=0;i<5;i++){
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.writeLock().lock();
                count++;
                System.out.println(Thread.currentThread().getName()+"写锁："+count);
                lock.writeLock().unlock();
            }
        }
    }
    static class ReadLockDemo implements Runnable{
        ReentrantReadWriteLock lock;
        public ReadLockDemo(ReentrantReadWriteLock lock){
            this.lock = lock;
        }
        @Override
        public void run() {
            for(int i=0;i<5;i++){
                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.readLock().lock();
                System.out.println(Thread.currentThread().getName()+"读锁："+count);
                lock.readLock().unlock();
            }

        }
    }
}
