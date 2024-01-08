package org.example.rpc.producer.impl;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/1 19:36
 */

import org.example.rpc.producer.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public String findById() {
        return "user{id=1,username=weige}";
    }

}
