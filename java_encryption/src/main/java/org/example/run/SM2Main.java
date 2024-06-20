package org.example.run;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.file.Sm2Files;
import picocli.CommandLine;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/6/19 22:04
 * -d E:\target -de
 */
@CommandLine.Command(name = "SM2加解密工具", mixinStandardHelpOptions = true, version = "1.0", description = "")
@Slf4j
public class SM2Main implements Runnable {

    @CommandLine.Option(names = {"-en"}, description = "加密", required = false)
    private Boolean en = false;

    @CommandLine.Option(names = {"-de"}, description = "解密", required = false)
    private Boolean de = false;

    @CommandLine.Option(names = {"-d", "--dir"}, description = "目录", required = true)
    private String dir;
    @Override
    public void run() {
        if (en == null && de == null) {
            log.info("必须指定加密还是解密");
            return;
        }
        if (!en && !de) {
            log.info("必须指定加密还是解密");
            return;
        }
        if (en && de) {
            log.info("加密和解密只能同时存在一个");
            return;
        }
        if(StringUtils.isBlank(dir)){
            log.info("加密的文件夹或者文件不能为空！");
            return;
        }
        if(!FileUtil.exist(dir)){
            log.info("填写的文件夹或者文件不存在！[{}]",dir);
            return;
        }
        try {
            if (en) {
                log.info("开始加密，目标文件夹：{}",dir);
                Sm2Files.encryptFiles(dir);
            }
            if (de) {
                log.info("开始解密，目标文件夹：{}",dir);
                Sm2Files.dencryptFiles(dir);
            }
        } catch (Exception e) {
            log.info("{}，加解密失败,密码错误，或者文件已经损坏:{}",dir,e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        int execute = new CommandLine(new SM2Main()).execute(args);
        System.exit(execute);
    }
}
