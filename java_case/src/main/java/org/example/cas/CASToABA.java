package org.example.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2023/12/23 15:29
 * 缺陷：循环时间太长、只能保证一个共享变量原子操作、与此同时ABA问题
 */
public class CASToABA {
    static AtomicInteger ar = new AtomicInteger(100);
    static AtomicStampedReference<Integer> ars = new AtomicStampedReference<Integer>(100, 1);

    public static void main(String[] args) throws Exception {
        System.out.println("=========ABA问题产生====");

        Thread t1 = new Thread(() -> {
            ar.compareAndSet(100, 101);
            ar.compareAndSet(101, 100);
        }, "t1");
        t1.start();
        Thread t2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(ar.compareAndSet(100, 2022) + "\t" + ar.get());
        }, "t2");
        t2.start();
        //顺序执行 AtomicInteger案例先执行
        t1.join();
        t2.join();
        System.out.println("=====ABA问题解决=====");
        new Thread(() -> {
            int stamp = ars.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t第一次版本号：" + stamp);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(ars.getStamp());
            boolean two_version = ars.compareAndSet(100, 101, ars.getStamp(), ars.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t第二次版本号：" + ars.getStamp() + "   当前实际的值：" + ars.getReference());
            System.out.println(Thread.currentThread().getName() + "\t修改成功与否：" + two_version + "    当前最新版本号：" + ars.getStamp());

            boolean three_version = ars.compareAndSet(101, 100, ars.getStamp(), ars.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "\t第三次版本号：" + ars.getStamp() + "   当前实际的值：" + ars.getReference());
            System.out.println(Thread.currentThread().getName() + "\t修改成功与否：" + three_version + "    当前最新版本号：" + ars.getStamp());
        }, "t3").start();

        new Thread(() -> {
            int stamp = ars.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t第一次版本号：" + stamp);
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            /**
             * 当版本号为最新且expected值域为最新的时候才能修改
             */
            boolean result = ars.compareAndSet(100, 2022, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + "\t修改成功与否：" + result + "    当前最新版本号" + ars.getStamp());
            System.out.println(Thread.currentThread().getName() + "\t当前实际值：" + ars.getReference());
        }, "t4").start();
    }
}
