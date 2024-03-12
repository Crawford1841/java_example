package org.example.single.hungry;

/**
 * 保持全局上下只有一个实例，类加载的时候直接实例化
 * 优点：线程安全，在类加载的时候就创建的实例，不存在多线程问题
 * 缺点：不支持延迟加载，即使实例没被使用，也会被创建
 */
public class HungrySingle {


    private HungrySingle(){
        // do something
    }

    private static final HungrySingle instance = new HungrySingle();


    public static HungrySingle getInstance(){
        return instance;
    }

}
