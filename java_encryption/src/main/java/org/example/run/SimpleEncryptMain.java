package org.example.run;

import cn.hutool.core.io.FileUtil;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.example.file.Sm2Files;
import picocli.CommandLine;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/6/19 22:04
 */
@CommandLine.Command(name = "SM2加密工具", mixinStandardHelpOptions = true, version = "1.0", description = "")
@Slf4j
public class SimpleEncryptMain implements Runnable {

    @Override
    public void run() {
        String dir = "";
        Scanner in = new Scanner(System.in);
        log.info("=========注意：单个文件的大小不能超过2Gb=============");
        while(true){
            log.info("请输入要加密的文件根目录");
            dir = in.nextLine();
            if(!FileUtil.exist(dir)){
                log.info("您输入的文件路径或者文件不存在，请重新输入！");
            }else{
                break;
            }
        }
        try {
            log.info("开始加密，目标文件夹：{}", dir);
            Sm2Files.encryptFiles(dir);
        } catch (Exception e) {
            log.info("{}，加密失败,密码错误，或者文件已经损坏:{}", dir, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        int execute = new CommandLine(new SimpleEncryptMain()).execute(args);
        System.exit(execute);
    }
}
