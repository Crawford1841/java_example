package org.example.rpc;

import org.example.rpc.producer.SkuService;
import org.example.rpc.producer.UserService;
import org.example.rpc.consumer.RPCProxy;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/1 22:30
 * 服务调用方
 */
public class TestRPCV1 {
    public static void main(String[] args) {
        //第一次远程调用
        SkuService skuService = (SkuService) RPCProxy.create(SkuService.class);

        //第二次远程调用
        UserService service = (UserService) RPCProxy.create(UserService.class);
        System.out.println(skuService.findName("uid"));
        System.out.println(service.findById());

    }
}
