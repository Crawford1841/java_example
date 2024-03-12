package org.example.single.lazy;

/**
 * 保持全局上下只有一个实例，类加载的时候直接实例化
 * 优点：延迟加载
 * 缺点：并发度低下
 */
public class LazySingle {

    private static LazySingle instance;
    private LazySingle(){
        //do something
    }

    public synchronized static LazySingle getInstance(){
        if(instance == null){
            instance = new LazySingle();
        }
        return instance;
    }

    /**
     * 双重检测锁模式
     * 低的jdk版本，会存在指令重排的问题，解决方式就是在instance成员变量上加上volatile，高版本jdk在内部已经自行解决。
     * @return
     */
    public static LazySingle getInstanceDobuleSyc(){
        if(instance == null){
            //开启类级锁
            synchronized (LazySingle.class){
                instance = new LazySingle();
            }
        }
        return instance;
    }

}
