package org.example.md5;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/6/20 19:57
 */

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TestMd5 {
    public static void main(String[] args){
        String md5 = computeMD5(new File("D:\\04-JVM虚拟机-课程笔记.pdf"));
        String md5_en = computeMD5(new File("D:\\SXdDd0pNaFVCcmlZSkpFS243T09EcWJMRFg5WXVUZUUxWmduMEpQcEUyZ0hFV3hCZklLSnV3PT0="));
        System.out.println(md5);
        System.out.println(md5_en);
    }


    private static String computeMD5(File file) {
        DigestInputStream din = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            //第一个参数是一个输入流
            din = new DigestInputStream(new BufferedInputStream(new FileInputStream(file)), md5);

            byte[] b = new byte[1024];
            while (din.read(b) != -1);

            byte[] digest = md5.digest();

            StringBuilder result = new StringBuilder(file.getName());
            result.append(": ");
            result.append(DatatypeConverter.printHexBinary(digest));
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (din != null) {
                    din.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
