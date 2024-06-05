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
import java.util.stream.Collectors;
import org.example.conf.Constatnt;

public class ZhConverTxt {

    public static void main(String[] args) throws IOException {
        List<File> files = FileUtil.loopFiles("D:\\basic_document\\DA会计\\01-零基础预习班-张敬富（23讲全）");
        List<File> collect = files.stream().filter(item -> Constatnt.exist(Constatnt.suffix_text, item))
                .collect(Collectors.toList());

        for(int i=0;i<collect.size();i++){
            File file = collect.get(i);
            BufferedReader utf8Reader = FileUtil.getUtf8Reader(file);
            String line;
            List<String> lines = new ArrayList<>();
            while((line = utf8Reader.readLine())!=null){
                String simpleCn = ZhConverterUtil.toSimple(line);
                System.out.println(simpleCn);
                lines.add(simpleCn);
            }
            System.out.println(file.getParent());
            FileUtil.writeUtf8Lines(lines,"D:\\basic_document\\DA会计\\01-零基础预习班-张敬富（23讲全）\\result\\"+file.getName());
        }
    }
}
