package org.example.future;

import java.util.Random;
import java.util.concurrent.*;

/**
 * FutureTask叫未来任务，可以将一个复杂的任务剔除出去交给另外一个线程来完成
 */
public class FutureCase {
    /**
     * get()
     * get方法的行为取决于Callable任务的状态，只有以下5种情况：
     *      1. 任务正常完成：get方法会立刻返回结果
     *      2. 任务尚未完成：任务还没有开始或进行中，get将阻塞并直到任务完成。
     *      3. 任务执行过程中抛出Exception：get方法会抛出ExecutionException，这里抛出异常，是call()执行时产生的那个异常
     *      4. 任务被取消：get方法会抛出CancellationException
     *      5. 任务超时：get方法有一个重写方法，是传入一个延迟时间的，如果时间到了还没有获得结果，get方法会抛出TimeoutException
     * get(long timeout,TimeUnit unit)
     *      如果call()在规定时间内完成任务，那么就会正常获取到返回值，而如果在指定时间内没有计算出结果，则会抛出TimeoutException
     * cancel()
     *      如果这个任务还没有开始执行，任务会被正常取消，未来也不会被执行，返回true
     *      如果任务已经完成或已经取消，则cancel()方法会执行失败，方法返回false
     *      如果这个任务已经开始，这个取消方法将不会直接取消该任务，而是会根据参数mayInterruptIfRunningg来做判断。如果是true,就会发出中断信号给这个任务。
     * isDone()
     *      判断线程是否执行完，执行完并不代表执行成功。
     * isCancelled()
     *      判断是否被取消
     */
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(10);
        Future<Integer> future =  service.submit(new CallableTask());
        try {
            System.out.println(future.get());
        }catch (Exception e){
            e.printStackTrace();
        }

        Task task = new Task();
        FutureTask<Integer> integerFutureTask = new FutureTask<>(task);
        service.submit(integerFutureTask);
        try {
            System.out.println("task运行结果："+integerFutureTask.get());
        }catch (Exception e){
            e.printStackTrace();
        }
        service.shutdown();
    }
}

class CallableTask implements Callable<Integer>{

    @Override
    public Integer call() throws Exception {
        Thread.sleep(3000);
        Integer tempInt = new Random().nextInt();
        System.out.println("执行完毕获取结果："+tempInt);
        return tempInt;
    }
}

class Task implements Callable<Integer>{

    @Override
    public Integer call() throws Exception {
        System.out.println("子线程正在计算");
        Thread.sleep(3000);
        int sum = 0;
        for(int i=0;i<100;i++){
            sum +=i;
        }
        return sum;
    }
}