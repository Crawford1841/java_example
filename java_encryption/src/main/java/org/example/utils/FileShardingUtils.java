package org.example.utils;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import java.util.Arrays;

/**
 * sm4算法
 */
public class FileShardingUtils {

    private static String SEPARATOR = "******file_encode_separator******";//文件分段加密分隔符

    private static int EN_LIMIT_SIZE = 1024 * 1024;//加密文件临界大小，1M.

    private static int DE_LIMIT_SIZE = 2 * 1024 * 1024;//解密文件临界大小，2M.

    public static final String PUBLIC_KEY = "csdn1024CSDN1024";

    public static final String PRIVATE_KEY = "";
    /**
     * 加密文件
     *
     * @return
     * @throws Exception
     */
    public static byte[] encryptFile(byte[] data) throws Exception {
        SymmetricCrypto sm4 = SmUtil.sm4(PUBLIC_KEY.getBytes());
        if (data.length > EN_LIMIT_SIZE) {//大于1M
            /**
             * 取前1M并且加密
             */
            byte[] data_1m = Arrays.copyOf(data, EN_LIMIT_SIZE);
            byte[] data_1m_en = sm4.encrypt(data_1m);
            /**
             * 读取剩余的文件
             */
            byte[] data_left = Arrays.copyOfRange(data, EN_LIMIT_SIZE, data.length);
            /**
             * 合并加密的1M文件和分隔符
             */
            byte[] data_1m_en_separator = byteMerger(data_1m_en, SEPARATOR.getBytes());
            /**
             * 填充至2M
             */
            byte[] data_padding = new byte[DE_LIMIT_SIZE - data_1m_en_separator.length];
            Arrays.fill(data_padding, (byte) 1);
            byte[] data_2m = byteMerger(data_1m_en_separator, data_padding);
            /**
             *生成最终加密文件
             */
            return byteMerger(data_2m, data_left);
        } else {//小于等于1M，全部加密
            return sm4.encrypt(data);
        }
    }

    /**
     * 解密文件
     *
     * @return
     */
    //public static byte[] decryptFile(byte[] data) throws Exception {
    //    SymmetricCrypto sm4 = SmUtil.sm4(PUBLIC_KEY.getBytes());
    //    if (data.length > DE_LIMIT_SIZE) {//大于2M
    //        /**
    //         * 取前2M并且解密
    //         */
    //        byte[] data_2m = Arrays.copyOf(data, DE_LIMIT_SIZE);
    //        int index_separator = searchInByteArray(data_2m, SEPARATOR.getBytes());
    //        if (index_separator > 0) {//存在分隔符
    //            byte[] data_pre_en = Arrays.copyOf(data_2m, index_separator);
    //            byte[] data_pre = SM2Util.decrypt(SM2Util.PRIVATE_KEY, data_pre_en);
    //            byte[] data_left = Arrays.copyOfRange(data, DE_LIMIT_SIZE, data.length);//取2M后的剩余部分文件
    //            return ArrayUtils.byteMerger(data_pre, data_left);//生成最终解密文件
    //        } else {
    //            return SM2Util.decrypt(SM2Util.PRIVATE_KEY, data);
    //        }
    //    } else {//文件小于2M，采用全部解密
    //        return SM2Util.decrypt(SM2Util.PRIVATE_KEY, data);
    //    }
    //}

    /**
     * 合并byte[]数组 （不改变原数组）
     * @param byte_1
     * @param byte_2
     * @return 合并后的数组
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

}
