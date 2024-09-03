package org.example.analysis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalysisLog {
    private static void find(List<String> logs,String fileName){
        if(CollectionUtil.isEmpty(logs)){
            return;
        }
        List<String> resLines = new ArrayList<>();
        Map<String,Log> logLine = new HashMap<>();
        logs.forEach(log->{
            // 正则表达式匹配响应时间
            String regex = "\\d+ ms";
            // 创建Pattern对象
            Pattern pattern = Pattern.compile(regex);
            // 创建Matcher对象
            Matcher matcher = pattern.matcher(log);
            // 查找并打印匹配结果
            if (matcher.find()) {
                //接口匹配
                String interRegex = "\"[A-Z]+ (/[^ ]*)";
                Pattern compile = Pattern.compile(interRegex);
                Matcher matcherInter = compile.matcher(log);
                matcherInter.find();
                String url = matcherInter.group(1);
                String parms = "";
                if(url.indexOf("?")>0){
                    parms = url.substring(url.lastIndexOf("?")+1);
                    url = url.substring(0,url.lastIndexOf("?"));
                }
                System.out.println("接口路径："+url);
                System.out.println("接口参数："+parms);
                System.out.println("响应时间: " + matcher.group());
                System.out.println("日志详情："+log);

                //响应时间
                Integer newMs = Integer.valueOf(matcher.group().replaceAll("ms", "").trim());
                if(newMs>100){
                    StringBuilder sb = new StringBuilder();
                    sb.append("接口："+url+"，");
                    sb.append("参数："+parms+"，");
                    sb.append("响应时间："+newMs);
                    if(logLine.containsKey(url)){
                        Log l = logLine.get(url);
                        if(newMs>l.getMs()){
                            l.setContent(sb.toString());
                        }
                    }else{
                        Log data = new Log();
                        data.setMs(newMs);
                        data.setContent(sb.toString());
                        logLine.put(url,data);
                    }
                }
            }
        });
        logLine.entrySet().forEach(item->{
            String log = item.getValue().getContent();
            resLines.add(log);
        });
        FileUtil.writeUtf8Lines(resLines,"E:\\新建文件夹\\日志统计\\log\\筛选\\"+fileName+".log");
    }
    public static void main(String[] args) {

        List<File> list = FileUtil.loopFiles("E:\\新建文件夹\\日志统计\\log");
        list.forEach(item->{
            List<String> strings = FileUtil.readUtf8Lines(item);
            find(strings,item.getName());
        });
    }
}
