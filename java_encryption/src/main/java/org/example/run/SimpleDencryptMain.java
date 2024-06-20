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
@CommandLine.Command(name = "SM2解密工具", mixinStandardHelpOptions = true, version = "1.0", description = "")
@Slf4j
public class SimpleDencryptMain implements Runnable {

    @Override
    public void run() {
        String dir = "";
        Scanner in = new Scanner(System.in);
        log.info("本软件仅用于学习交流使用，使用软件不当，造成的损失由使用者自行承担。\n"
                + "版权与免责声明的最终解释权归软件作者所有！");

        while(true){
            log.info("请输入要解密的文件根目录");
            dir = in.nextLine();
            if(!FileUtil.exist(dir)){
                log.info("您输入的文件路径或者文件不存在，请重新输入！");
            }else{
                break;
            }
        }
        try {
            log.info("开始解密，目标文件夹：{}", dir);
            Sm2Files.dencryptFiles(dir);
        } catch (Exception e) {
            log.info("{}，解密失败,密码错误，或者文件已经损坏:{}", dir, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        int execute = new CommandLine(new SimpleDencryptMain()).execute(args);
        System.exit(execute);
    }
}
