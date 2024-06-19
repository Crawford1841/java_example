package org.example.utils;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.util.Arrays;

/**
 * 文件分片加密——sm2算法
 * TODO 暂未完成
 */
public class FileShardingUtils {

    private static String SEPARATOR = "******file_encode_separator******";//文件分段加密分隔符

    private static int EN_LIMIT_SIZE = 1024 * 1024;//加密文件临界大小，1M.

    private static int DE_LIMIT_SIZE = 2 * 1024 * 1024;//解密文件临界大小，2M.

    public static final String PUBLIC_KEY = "04d2f6e0b7c272994f50c27a291d8ba38395dde8ac091cd72dd1fbf7f296c69060d0701594dd9e5877e12fef2afede1ad9ff5671e66fb90daf5f9257114b4147cc";
    public static final String PRIVATE_KEY = "959c8ccc6b06dab71dab023d95ef7ef5717174f68e1d45f2ddc9835b73bcef40";

    /**
     * 加密文件
     *
     * @return
     * @throws Exception
     */
    public static byte[] encryptFile(byte[] data) throws Exception {
        SM2 sm2 = SmUtil.sm2(PRIVATE_KEY.getBytes(), PUBLIC_KEY.getBytes());
        if (data.length > EN_LIMIT_SIZE) {//大于1M
            /**
             * 取前1M并且加密
             */
            byte[] data_1m = Arrays.copyOf(data, EN_LIMIT_SIZE);
            byte[] data_1m_en = sm2.encrypt(data_1m);
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
            return sm2.encrypt(data);
        }
    }

    /**
     * 解密文件
     *
     * @return
     */
    public static byte[] decryptFile(byte[] data) throws Exception {
        SM2 sm2 = SmUtil.sm2(PRIVATE_KEY.getBytes(), PUBLIC_KEY.getBytes());
        if (data.length > DE_LIMIT_SIZE) {//大于2M
            /**
             * 取前2M并且解密
             */
            byte[] data_2m = Arrays.copyOf(data, DE_LIMIT_SIZE);
            int index_separator = searchInByteArray(data_2m, SEPARATOR.getBytes());
            if (index_separator > 0) {//存在分隔符
                byte[] data_pre_en = Arrays.copyOf(data_2m, index_separator);
                byte[] data_pre = sm2.decrypt(data_pre_en);
                byte[] data_left = Arrays.copyOfRange(data, DE_LIMIT_SIZE, data.length);//取2M后的剩余部分文件
                return byteMerger(data_pre, data_left);//生成最终解密文件
            } else {
                return sm2.decrypt(data);
            }
        } else {//文件小于2M，采用全部解密
            return sm2.decrypt(data);
        }
    }

    /**
     * 合并byte[]数组 （不改变原数组）
     *
     * @param byte_1
     * @param byte_2
     * @return 合并后的数组
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    /**
     * 在字节数组中查找指定子数组的起始位置
     *
     * @param array      要搜索的字节数组
     * @param subArray   要查找的子数组
     * @return 子数组在主数组中的起始位置，如果未找到，则返回 -1
     */
    public static int searchInByteArray(byte[] array, byte[] subArray) {
        for (int i = 0; i <= array.length - subArray.length; i++) {
            boolean found = true;
            for (int j = 0; j < subArray.length; j++) {
                if (array[i + j] != subArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {



    }

}
