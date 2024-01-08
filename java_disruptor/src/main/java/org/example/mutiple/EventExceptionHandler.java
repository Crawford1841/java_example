package org.example.mutiple;

import com.lmax.disruptor.ExceptionHandler;
import org.example.entity.Order;

public class EventExceptionHandler implements ExceptionHandler<Order> {
    //消费时出现异常
    @Override
    public void handleEventException(Throwable throwable, long l, Order order) {
        System.out.println("消费出现异常");
    }
    //启动时出现异常
    @Override
    public void handleOnStartException(Throwable throwable) {

    }
    //停止时出现异常
    @Override
    public void handleOnShutdownException(Throwable throwable) {

    }
}
