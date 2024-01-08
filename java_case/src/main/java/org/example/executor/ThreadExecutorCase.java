package org.example.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadExecutorCase {
    public static void main(String[] args) {
//        fixedThread();
        singleThread();
        cacheThread();
        scheduleThread();

    }

    /**
     * 固定线程数量
     */
    private static void fixedThread(){
        ExecutorService service = Executors.newFixedThreadPool(4);
        for(int i=0;i<1000;i++){
            service.execute(new Task());
        }
    }

    /**
     * 只有一个线程
     */
    private static void singleThread(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for(int i=0;i<1000;i++){
            executorService.execute(new Task());
        }
    }
    /**
     * 可缓存线程池，它是无界线程池，并具有自动回收多余线程的功能。
     */
    private static void cacheThread(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<1000;i++){
            executorService.execute(new Task());
        }
    }
    /**
     * 支持定时及周期性任务执行的线程池
     */
    private static void scheduleThread(){
        //先延迟1秒运行，然后每隔3秒运行一次
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
        scheduledExecutorService.scheduleAtFixedRate(new Task(),1,3,TimeUnit.SECONDS);


    }


}

class Task implements Runnable{

    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName());
    }
}