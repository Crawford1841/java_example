package org.example.aes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import picocli.CommandLine;

/**
 * 加密
 * 对当前目录下，以 .java 结尾的文件进行加密，密码是Aa111111
 *
 * java -jar .\AesTool.jar -d ./ -f ".*.java$" -p Aa111111 -en
 *
 * 解密
 * 对当前目录下，以 .gpg 结尾的文件进行解密，密码是 Aa111111
 *
 * java -jar .\AesTool.jar -d ./ -f ".*.gpg$" -p Aa111111 -de
 */
@CommandLine.Command(name = "Aes 加解密工具", mixinStandardHelpOptions = true, version = "1.0", description = "")
public class Main  implements  Runnable{
    @CommandLine.Option(names = {"-p", "--password"}, description = "密码，小于等于16位", required = true,interactive = true, arity = "0..1", hidden = true)
    private String password;

    @CommandLine.Option(names = {"-en"} , description = "加密", required = false)
    private Boolean en = false;

    @CommandLine.Option(names = {"-de"} , description = "解密", required = false)
    private Boolean de = false;

    @CommandLine.Option(names = {"-d", "--dir"}, description = "目录", required = true)
    private String dir;
    @CommandLine.Option(names = {"-f", "--file"}, description = "文件名，支持正则表达式，例： java -jar .\\AesTool.jar -d ./ -f \".*.java$\" -p Aa111111 -en", required = true)
    private String files;

    private Pattern pattern;
    private Matcher matcher;
    List<String> allFileList = new ArrayList<>();


    @Override
    public void run() {
        if(en == null && de == null ){
            System.out.println("必须指定加密还是解密");
            return;
        }
        if(!en && !de){
            System.out.println("必须指定加密还是解密");
            return;
        }
        if(en && de  ){
            System.out.println("加密和解密只能同时存在一个");
            return;
        }

        pattern = Pattern.compile(files);
        recursiveListFiles(new File(dir), pattern);
        for (String file : allFileList) {
            try {
                if(en){
                    System.out.println("加密: " + file);
                    AesUtil.aesEn(password, file);
                }
                if(de){
                    System.out.println("解密: " + file);
                    AesUtil.aesDe(password, file);
                }
            } catch (Exception e) {
                System.out.println((file + " 加解密失败,密码错误，或者密文已经损坏:\n " + e.getMessage()));
                throw new RuntimeException(e);
            }
        }
    }

    private  void recursiveListFiles(File directory, Pattern pattern) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                 matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {
                    allFileList.add(file.getAbsolutePath());
                }
            } else if (file.isDirectory()) {
                recursiveListFiles(file, pattern);
            }
        }
    }

    public static void main(String[] args) {
//        CommandLine.run(new Main(), args);
        int execute = new CommandLine(new Main()).execute(args);
        System.exit(execute);

    }
}