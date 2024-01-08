package org.example.collection;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 是平时查询的时候，都不需要加锁，随便访问，只有在更新的时候，才会从原来的
 * 数据复制一个副本出来，然后修改这个副本，最后把原数据替换成当前的副本。修改操作的同时，读操作不会被阻塞，而是继续读取旧的数据。这点要跟读写锁区分一下。
 * 底层使用数组存储数据，使用复制副本实现有锁写操作，不能保证强一致性。适合于读多写少，允许读写数据短暂不一致的高并发场景
 */
public class CopyOnWirteArrayListCase {
    /**
     * 优点
     *      对于一些读多写少的数据，写入时复制的做法就很不错，例如：配置、黑名单、物流地址等变化非常少的数据，这是一种无锁的实现。可以帮我们实现程序更高的并发。
     *      CopyOnWriteArrayList 并发安全且性能比 Vector 好。Vector 是增删改查方法都加了
     *      synchronized 来保证同步，但是每个方法执行的时候都要去获得锁，性能就会大大下降，而
     *      CopyOnWriteArrayList 只是在增删改上加锁，但是读不加锁，在读方面的性能就好于 Vector。
     * 缺点
     *      数据一致性问题。这种实现只是保证数据的最终一致性，在添加到拷贝数据而还没进行替换的时候，读到的仍然是旧数据。
     *      内存占用问题。如果对象比较大，频繁地进行替换会消耗内存，从而引发 Java 的 GC 问题，这个时候，我们应该考虑其他的容器，例如 ConcurrentHashMap。
     */
    public static void main(String[] args) {
        List<Integer> tempList = Arrays.asList(new Integer[]{1,2});
        CopyOnWriteArrayList<Integer> copyList = new CopyOnWriteArrayList<>(tempList);

        //模拟多线程对list进行读写
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(new ReadThread(copyList));
        executorService.execute(new WriteThread(copyList));
        executorService.execute(new WriteThread(copyList));
        executorService.execute(new WriteThread(copyList));
        executorService.execute(new ReadThread(copyList));
        executorService.execute(new WriteThread(copyList));
        executorService.execute(new ReadThread(copyList));
        executorService.execute(new WriteThread(copyList));
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("copyList size："+copyList.size());
        executorService.shutdown();

    }
}
class ReadThread implements Runnable{
    private List<Integer> list;
    public ReadThread(List<Integer> list){
        this.list = list;
    }
    @Override
    public void run() {
        System.out.println("size:="+list.size()+",::");
        for (Integer ele:list) {
            System.out.print(ele+",");
        }
        System.out.println();
    }
}

class WriteThread implements Runnable{
    private List<Integer> list;
    public WriteThread(List<Integer> list){
        this.list = list;
    }
    @Override
    public void run() {
        this.list.add(9);
        System.out.println(Thread.currentThread().getName()+"新增了元素，当前元素总数为:"+this.list.size());
    }
}