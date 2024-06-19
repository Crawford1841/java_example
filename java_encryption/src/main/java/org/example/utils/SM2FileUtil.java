package org.example.utils;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/6/19 20:20
 */

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.DES;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import sun.misc.BASE64Decoder;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件加解密——sm2
 * 文件名加解密——DES
 */
@Slf4j
public class SM2FileUtil {

    /**
     * 公钥常量
     */
    public static final String KEY_PUBLIC_KEY = "publicKey";
    /**
     * 私钥返回值常量
     */
    public static final String KEY_PRIVATE_KEY = "privateKey";

    public static final String PUBLIC_KEY = "04d2f6e0b7c272994f50c27a291d8ba38395dde8ac091cd72dd1fbf7f296c69060d0701594dd9e5877e12fef2afede1ad9ff5671e66fb90daf5f9257114b4147cc";
    public static final String PRIVATE_KEY = "959c8ccc6b06dab71dab023d95ef7ef5717174f68e1d45f2ddc9835b73bcef40";

    public static final String key = "20240619";
    // iv：偏移量，ECB模式不需要，CBC模式下必须为8位
    public static final String iv = "huangwei";

    /**
     * 生成SM2公私钥
     *
     * @return
     */
    public static Map<String, String> generateSm2Key() {
        SM2 sm2 = new SM2();
        ECPublicKey publicKey = (ECPublicKey) sm2.getPublicKey();
        ECPrivateKey privateKey = (ECPrivateKey) sm2.getPrivateKey();
        // 获取公钥
        byte[] publicKeyBytes = publicKey.getQ().getEncoded(false);
        String publicKeyHex = HexUtil.encodeHexStr(publicKeyBytes);

        // 获取64位私钥
        String privateKeyHex = privateKey.getD().toString(16);
        // BigInteger转成16进制时，不一定长度为64，如果私钥长度小于64，则在前方补0
        StringBuilder privateKey64 = new StringBuilder(privateKeyHex);
        while (privateKey64.length() < 64) {
            privateKey64.insert(0, "0");
        }

        Map<String, String> result = new HashMap<>();
        result.put(KEY_PUBLIC_KEY, publicKeyHex);
        result.put(KEY_PRIVATE_KEY, privateKey64.toString());
        return result;
    }

    /**
     * SM2私钥签名
     *
     * @param privateKey 私钥
     * @param content    待签名内容
     * @return 签名值
     */
    public static String sign(String privateKey, String content) {
        SM2 sm2 = new SM2(privateKey, null);
        return sm2.signHex(HexUtil.encodeHexStr(content));
    }

    /**
     * SM2公钥验签
     *
     * @param publicKey 公钥
     * @param content   原始内容
     * @param sign      签名
     * @return 验签结果
     */
    public static boolean verify(String publicKey, String content, String sign) {
        SM2 sm2 = new SM2(null, publicKey);
        return sm2.verifyHex(HexUtil.encodeHexStr(content), sign);
    }

    /**
     * SM2公钥加密
     *
     * @param content   原文
     * @param publicKey SM2公钥
     * @return
     */
    public static String encryptBase64(String content, String publicKey) {
        SM2 sm2 = new SM2(null, publicKey);
        return sm2.encryptBase64(content, KeyType.PublicKey);
    }

    /**
     * SM2私钥解密
     *
     * @param encryptStr SM2加密字符串
     * @param privateKey SM2私钥
     * @return
     */
    public static String decryptBase64(String encryptStr, String privateKey) {
        SM2 sm2 = new SM2(privateKey, null);
        return StrUtil.utf8Str(sm2.decrypt(encryptStr, KeyType.PrivateKey));
    }

    /**
     * SM2 文件加密
     *
     * @param publicKey     公钥
     * @param dataBytes     提交的原始文件以流的形式
     * @param outputPath    输出的加密文件路径
     * @param fileName      输出的加密文件名称
     */
    public static Boolean lockFile(String publicKey, byte[] dataBytes, String outputPath, String fileName) throws Exception {
        Boolean flag = false;
        if (StringUtils.isEmpty(publicKey) || null == dataBytes ||
                StringUtils.isEmpty(outputPath) || StringUtils.isEmpty(fileName)) {
            throw new RuntimeException("缺少必要参数!");
        } else {
            long startTime = System.currentTimeMillis();
            // 初始化SM2对象
            try {
                SM2 SM_2 = new SM2(null, publicKey);
                byte[] data;
                data = SM_2.encrypt(dataBytes, KeyType.PublicKey);
                FileUtils.byteToFile(data, outputPath, fileName);
                flag = true;
            } catch (Exception e) {
                flag = false;
                log.error("Exception | " + e);
            }
            long endTime = System.currentTimeMillis();
            log.error("本次加密操作,所耗时间为：" + (endTime - startTime));
            return flag;
        }
    }

    /**
     * SM2 文件解密
     *
     * @param privateKey   私钥
     * @param lockFilePath 加密文件路径
     * @param outputPath   输出的解密文件路径
     * @param fileName     输出的解密文件名称
     */
    public static Boolean unlockFile(String privateKey, String lockFilePath, String outputPath, String fileName) {
        Boolean flag = false;
        if (StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(lockFilePath) || StringUtils.isEmpty(fileName)) {
            throw new RuntimeException("缺少必要参数!");
        } else {
            long startTime = System.currentTimeMillis();
            try {
                // 初始化SM2对象
                SM2 SM_2 = new SM2(privateKey, null);
                byte[] bytes = FileUtils.fileToByte(lockFilePath);
                byte[] data;
                data = SM_2.decrypt(bytes, KeyType.PrivateKey);
                FileUtils.byteToFile(data, outputPath, fileName);
                flag = true;
            } catch (Exception e) {
                log.error("Exception | " + e);
            }
            long endTime = System.currentTimeMillis();
            log.error("本次解密操作,所耗时间为：" + (endTime - startTime));
            return flag;
        }
    }

    /**
     * 通过私钥进行文件签名
     *
     * @param privateKey    私钥
     * @param dataBytes 需要签名的文件以流的形式
     * @throws Exception
     */
    public static String generateFileSignByPrivateKey(String privateKey, byte[] dataBytes) throws Exception {
        String signature = "";
        if (StringUtils.isEmpty(privateKey) || null == dataBytes) {
            throw new RuntimeException("缺少必要参数!");
        } else {
            long startTime = System.currentTimeMillis();
            String signs = "";
            try {
                //----------------------20210830优化:私钥HEX处理---------------------------------
                byte[] decode = Base64.decode(privateKey);
                SM2 sm3 = new SM2(decode,null);
                byte[] bytes = BCUtil.encodeECPrivateKey(sm3.getPrivateKey());
                String privateKeyHex = HexUtil.encodeHexStr(bytes);
                //------@End----------------20210830优化:私钥HEX处理------------------------------
                //需要加密的明文,得到明文对应的字节数组
                ECPrivateKeyParameters privateKeyParameters = BCUtil.toSm2Params(privateKeyHex);
                //创建sm2 对象
                SM2 sm2 = new SM2(privateKeyParameters, null);
                //这里需要手动设置，sm2 对象的默认值与我们期望的不一致 , 使用明文编码
                sm2.usePlainEncoding();
                sm2.setMode(SM2Engine.Mode.C1C2C3);
                byte[] sign = sm2.sign(dataBytes, null);
                //change encoding : hex to base64
                signs = Base64.encode(sign);
                signature = signs;
            } catch (Exception e) {
                log.error("Exception | " + e);
            }
            long endTime = System.currentTimeMillis();
            log.error("本次签名操作,所得签名为:" + signs + ",所耗时间为：" + (endTime - startTime));
            return signature;
        }
    }

    /**
     * 通过公钥进行文件验签
     *
     * @param publicKey 公钥
     * @param sign 签名（原先为hex处理后的16位，现在改为base处理后的64位）
     * @param dataBytes 需要验签的文件数据以流的形式
     * @return
     * @throws Exception
     */
    public static Boolean verifyFileSignByPublicKey(String publicKey, String sign, byte[] dataBytes) throws Exception {
        if (StringUtils.isEmpty(publicKey) || StringUtils.isEmpty(sign) || null == dataBytes) {
            throw new RuntimeException("缺少必要参数!");
        } else {
            long startTime = System.currentTimeMillis();
            Boolean verify = false;
            try {
                //-----------------------------20210830修改公钥HEX处理----------------------------
                byte[] decode = Base64.decode(publicKey);
                SM2 sm3 = new SM2(null, decode);
                byte[] bytes = ((BCECPublicKey) sm3.getPublicKey()).getQ().getEncoded(false);
                String publicKeyHex = HexUtil.encodeHexStr(bytes);
                //--------@End---------------------公钥HEX处理------------------------------------
                //需要加密的明文,得到明文对应的字节数组
                //这里需要根据公钥的长度进行加工
                if (publicKeyHex.length() == 130) {
                    //这里需要去掉开始第一个字节 第一个字节表示标记
                    publicKeyHex = publicKeyHex.substring(2);
                }
                String xhex = publicKeyHex.substring(0, 64);
                String yhex = publicKeyHex.substring(64, 128);
                ECPublicKeyParameters ecPublicKeyParameters = BCUtil.toSm2Params(xhex, yhex);
                //创建sm2 对象
                SM2 sm2 = new SM2(null, ecPublicKeyParameters);
                //这里需要手动设置，sm2 对象的默认值与我们期望的不一致 , 使用明文编码
                sm2.usePlainEncoding();
                sm2.setMode(SM2Engine.Mode.C1C2C3);
                verify = sm2.verify(dataBytes, Base64.decode(sign));
            } catch (Exception e) {
                log.error("Exception | " + e);
            }
            long endTime = System.currentTimeMillis();
            log.error("本次验签操作,所得结果为:" + verify + ",所耗时间为：" + (endTime - startTime));
            return verify;
        }
    }

    public static void main(String[] args) throws Exception {
//        String text = "====测ab试加解密=======";
//        Map<String, String> stringStringMap = generateSm2Key();
//        System.out.println("公钥："+stringStringMap.get(KEY_PUBLIC_KEY));
//        System.out.println("私钥："+stringStringMap.get(KEY_PRIVATE_KEY));
//        String encryptBase64 = encryptBase64(text, stringStringMap.get(KEY_PUBLIC_KEY));
//        String decryptBase64 = decryptBase64(encryptBase64, stringStringMap.get(KEY_PRIVATE_KEY));
//        System.out.println(encryptBase64);
//        System.out.println(decryptBase64);


        DES des = new DES(Mode.CBC, Padding.PKCS5Padding, key.getBytes(), iv.getBytes());
        String encrypt = Base64.encode(des.encryptBase64("10_固定资产加解密.mp4"));
        System.out.println("加密名称："+encrypt);

        byte[] bytes = FileUtils.fileToByte("D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\10_固定资产（2）.mp4");
        lockFile(PUBLIC_KEY,bytes,"D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）",encrypt);


        BASE64Decoder decoder = new BASE64Decoder();
        String str = new String(decoder.decodeBuffer(encrypt), "UTF-8");
        String decrypt = des.decryptStr(str);
        System.out.println("解密名称："+decrypt);

        unlockFile(PRIVATE_KEY,"D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\"+encrypt,
                "D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）",
                decrypt);


    }
}
