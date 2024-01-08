package org.example.rpc.producer.impl;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/1/1 19:34
 */

import org.example.rpc.producer.SkuService;

public class SkuServiceImpl implements SkuService {
    @Override
    public String findName(String name) {
        return "sku{}："+name;
    }
}
