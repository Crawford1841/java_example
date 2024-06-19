package org.example.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * @Author 国密4工具类
 * @Description 数据加密解密
 **/
public class Sm4Utils {
    public static final String PUBLIC_KEY = "csdn1024CSDN1024";

    private Sm4Utils(){}

    /**
     * sm4数据加密
     * @param secretKey 秘钥
     * @param params 参数信息
     * @return 加密后的值
     */
    public static String sm4EncryptUtil(String secretKey, String params){
        SymmetricCrypto sm4 = SmUtil.sm4(secretKey.getBytes());
        return sm4.encryptHex(params);
    }

    /**
     * sm4数据解密
     * @param secretKey 秘钥
     * @param encryptContext 加密的内容
     * @return 解密后的值
     */
    public static String sm4DecryptUtil(String secretKey, String encryptContext){
        SymmetricCrypto sm4 = SmUtil.sm4(secretKey.getBytes());
        return sm4.decryptStr(encryptContext, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 测试方法，测试完要记得删除掉
     */
    public static void main(String[] args) {
        // 自定义秘钥
        String secretKey = "csdn1024CSDN1024";
        String strParams = "{'name':'huangwei','age':'26'}";

        System.out.println(String.format("明文参数: %s", strParams));

        String encryptContext = sm4EncryptUtil(secretKey, strParams);
        System.out.println(String.format("加密后的值: %s", encryptContext));

        String decryptInfo = sm4DecryptUtil(secretKey, encryptContext);
        System.out.println(String.format("解密后的信息: %s", decryptInfo));

    }

}