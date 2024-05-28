package org.example.txt;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/5/28 23:28
 * 繁体转简体
 */

import cn.hutool.core.io.FileUtil;
import com.github.houbb.opencc4j.util.ZhConverterUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZhConverTxt {

    public static void main(String[] args) throws IOException {
        List<File> files = FileUtil.loopFiles("D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\result3");
        for(int i=0;i<files.size();i++){
            File file = files.get(i);
            BufferedReader utf8Reader = FileUtil.getUtf8Reader(file);
            String line;
            List<String> lines = new ArrayList<>();
            while((line = utf8Reader.readLine())!=null){
                String simpleCn = ZhConverterUtil.toSimple(line);
                System.out.println(simpleCn);
                lines.add(simpleCn);
            }
            System.out.println(file.getParent());
            FileUtil.writeUtf8Lines(lines,"D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\result3\\test\\"+file.getName());
        }
    }
}
