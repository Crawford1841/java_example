package org.example.threadlocal;

/**
 * 在 Java 的多线程并发执行过程中，为保证多个线程对变量的安
 * 全访问，可以将变量放到ThreadLocal 类型的对象中，使变量在每个线程中都有独立值，不会出现一个
 * 线程读取变量时而被另一个线程修改的现象。
 * ThreadLocal 是解决线程安全问题一个较好方案，它通过为每个线程提供一个独立的本地值，去解决并
 * 发访问的冲突问题。很多情况下，使用 ThreadLocal 比直接使用同步机制（如 synchronized）解决线
 * 程安全问题更简单，更方便，且结果程序拥有更高的并发性
 * <p>
 * ThreadLocal在Spring中作用巨大，在管理Request作用域中的Bean、事务、任务调度、AOP等模
 * 块都有它。
 * Spring中绝大部分Bean都可以声明成Singleton作用域，采用ThreadLocal进行封装，因此有状态
 * 的Bean就能够以singleton的方式在多线程中正常工作了。
 */
public class ThreadLocalCase {
    /**
     * 跨函数传递数据”场景典型案例：可以每个线程绑定一个 Session（用户会话）信息，这样一个线程的
     * 所有调用到的代码，都可以非常方便地访问这个本地会话，而不需要通过参数传递
     */
    // 用户信息 线程本地变量
//    private static final ThreadLocal<UserDTO> sessionUserLocal = new
//            ThreadLocal<>("sessionUserLocal");
//    // session 线程本地变量
//    private static final ThreadLocal<HttpSession> sessionLocal = new
//            ThreadLocal<>("sessionLocal");
////...省略其他
//    /**
//     *保存 session 在线程本地变量中
//     */
//    public static void setSession(HttpSession session){
//        sessionLocal.set(session);
//    }
//    /**
//     * 取得绑定在线程本地变量中的 session
//     */
//    public static HttpSession getSession() {
//        HttpSession session = sessionLocal.get();
//        Assert.notNull(session, "session 未设置");
//        return session;
//    }
////...省略其他
}
