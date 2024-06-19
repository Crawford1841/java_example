package org.example.file;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/6/19 22:09
 * 对文件的根目录下的所有文件及文件文件名、文件内容进行加解密
 */

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.utils.SM2FileUtil;

import java.io.File;
import java.util.List;

import static org.example.utils.SM2FileUtil.PRIVATE_KEY;
import static org.example.utils.SM2FileUtil.PUBLIC_KEY;

@Slf4j
public class Sm2Files {
    /**
     * 递归加密文件夹及文件名
     *
     * @param path
     */
    public static void encryptByFileNames(String path) {
        File rootDir = new File(path);
        if (!rootDir.exists()) {
            log.info("该文件不存在！");
        }
        if (rootDir.exists() && rootDir.isDirectory()) {
            encryptByFileNames(rootDir);
        } else {
            encryptByFileName(rootDir);
        }
    }

    /**
     * 递归加密文件夹及文件名
     *
     * @param file 当前文件或文件夹
     */
    private static void encryptByFileNames(File file) {
        if (file.isDirectory()) {
            // 先重命名子文件和子文件夹
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    encryptByFileNames(child);
                }
            }
        }
        encryptByFileName(file);
    }

    private static void encryptByFileName(File file) {
        // 加密当前文件或文件夹
        String fileName = file.getName();
        String encrypt = SM2FileUtil.fileNameDESEncrypt(fileName);
        File newFile = new File(file.getParentFile(), encrypt);
        if (!newFile.equals(file)) {
            File renamed = FileUtil.rename(file, encrypt, true);
            log.info("Renamed: {} -> {}", file.getAbsolutePath(), newFile.getAbsolutePath());
        }
    }


    /**
     * 递归解密文件夹及文件名
     *
     * @param path
     */
    public static void dencryptByFileNames(String path) {
        File rootDir = new File(path);
        if (!rootDir.exists()) {
            log.info("该文件不存在！");
        }
        if (rootDir.isDirectory()) {
            dencryptByFileNames(rootDir);
        } else {
            dencryptByFileName(rootDir);
        }
    }

    private static void dencryptByFileNames(File file) {
        if (file.isDirectory()) {
            // 先重命名子文件和子文件夹
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    dencryptByFileNames(child);
                }
            }
        }
        dencryptByFileName(file);
    }

    private static void dencryptByFileName(File file) {
        // 重命名当前文件或文件夹
        String fileName = file.getName();
        String encrypt = SM2FileUtil.fileNameDESDencrypt(fileName);
        File newFile = new File(file.getParentFile(), encrypt);
        if (!newFile.equals(file)) {
            File renamed = FileUtil.rename(file, encrypt, true);
            log.info("Renamed: {} -> {}", file.getAbsolutePath(), newFile.getAbsolutePath());
        }
    }


    /**
     * 递归加密文件及文件名和文件内容
     *
     * @param path 文件根路径
     */
    public static void encryptFiles(String path) {
        File rootDir = new File(path);
        if (!rootDir.exists()) {
            log.info("文件不存在");
        }
        if (rootDir.isDirectory()) {
            encryptFiles(rootDir);
        } else {
            encryptFile(rootDir);
        }
    }

    private static void encryptFiles(File file) {
        if (file.isDirectory()) {
            // 先重命名子文件和子文件夹
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    encryptFiles(child);
                }
            }
        }
        encryptFile(file);
    }

    private static void encryptFile(File file) {
        // 解密当前文件或文件夹
        String fileName = file.getName();
        String encrypt = SM2FileUtil.fileNameDESEncrypt(fileName);
        File newFile = new File(file.getParentFile(), encrypt);
        if (!newFile.equals(file)) {
            File renamed = FileUtil.rename(file, encrypt, true);
            if (renamed.isFile()) {
                byte[] bytes = FileUtil.readBytes(renamed);
                log.info("目标文件：{},保存目录：{}",renamed.getAbsolutePath(), renamed.getParentFile().getAbsolutePath());
                SM2FileUtil.lockFile(PUBLIC_KEY, bytes, renamed.getParentFile().getAbsolutePath(), encrypt);
            }
        }
    }

    /**
     * 递归解密文件及文件名和文件内容
     *
     * @param path 文件根路径
     */
    public static void dencryptFiles(String path) {
        File rootDir = new File(path);
        if (!rootDir.exists()) {
            log.info("文件不存在");
        }
        if (rootDir.isDirectory()) {
            dencryptFiles(rootDir);
        } else {
            dencryptFile(rootDir);
        }
    }

    /**
     * 递归加密文件及文件名和文件内容
     *
     * @param file
     */
    private static void dencryptFiles(File file) {
        if (file.isDirectory()) {
            // 先重命名子文件和子文件夹
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    dencryptFiles(child);
                }
            }
        }
        dencryptFile(file);
    }

    private static void dencryptFile(File file) {
        // 解密当前文件或文件夹
        String fileName = file.getName();
        String dencrypt = SM2FileUtil.fileNameDESDencrypt(fileName);
        log.info("解密文件名称：{}",dencrypt);
        File newFile = new File(file.getParentFile(), dencrypt);
        if (!newFile.equals(file)) {
            File renamed = FileUtil.rename(file, dencrypt, true);
            if (renamed.isFile()) {
                log.info("目标文件：{},保存目录：{}",renamed.getAbsolutePath(), renamed.getParentFile().getAbsolutePath());
                SM2FileUtil.unlockFile(PRIVATE_KEY, renamed.getAbsolutePath(), renamed.getParentFile().getAbsolutePath(), dencrypt);
            }
        }
    }

    public static void main(String[] args) {
//        encryptByFileNames("C:\\Users\\binary\\Desktop\\递归测试");
//        dencryptByFileNames("C:\\Users\\binary\\Desktop\\ejJVM3pFUVE0NzFqMWdBdWVMaDVMdz09");


        encryptFiles("C:\\Users\\binary\\Desktop\\递归测试");
        dencryptFiles("C:\\Users\\binary\\Desktop\\ejJVM3pFUVE0NzFqMWdBdWVMaDVMdz09");
    }
}
