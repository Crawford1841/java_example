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
    static List<String> travelLines = new ArrayList<>(200);
    static List<String> operateLines = new ArrayList<>(200);
    static List<String> consoleLines = new ArrayList<>(200);
    static Map<String,Log> travelLine = new HashMap<>(200);
    static Map<String,Log> operateLine = new HashMap<>(200);
    static Map<String,Log> consoleLine = new HashMap<>(200);
    private static void find(List<String> logs){
        if(CollectionUtil.isEmpty(logs)){
            return;
        }
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
                //System.out.println("日志详情："+log);

                //响应时间
                Integer newMs = Integer.valueOf(matcher.group().replaceAll("ms", "").trim());
                if(newMs>100){
                    StringBuilder sb = new StringBuilder();
                    sb.append("接口："+url+"，");
                    sb.append("参数："+parms+"，");
                    sb.append("响应时间："+newMs+"ms");
                    if(url.indexOf("/travel")>0){
                        put(travelLine,newMs,sb.toString(),url);
                    }else if(url.indexOf("/console")>0){
                        put(consoleLine,newMs,sb.toString(),url);
                    }else{
                        put(operateLine,newMs,sb.toString(),url);
                    }
                }
            }
        });
    }
    private static void put(Map<String,Log> logLine,int newMs,String content,String url){
        if(logLine.containsKey(url)){
            Log l = logLine.get(url);
            if(newMs>l.getMs()){
                l.setContent(content);
            }
        }else{
            Log data = new Log();
            data.setMs(newMs);
            data.setContent(content);
            logLine.put(url,data);
        }
    }
    public static void main(String[] args) {

        List<File> list = FileUtil.loopFiles("E:\\新建文件夹\\新建文件夹\\2024-09-04.log");
        list.forEach(item->{
            List<String> strings = FileUtil.readUtf8Lines(item);
            find(strings);
        });
        travelLine.entrySet().forEach(item->{
            String log = item.getValue().getContent();
            travelLines.add(log);
        });
        consoleLine.entrySet().forEach(item->{
            String log = item.getValue().getContent();
            consoleLines.add(log);
        });
        operateLine.entrySet().forEach(item->{
            String log = item.getValue().getContent();
            operateLines.add(log);
        });
        FileUtil.writeUtf8Lines(travelLines,"E:\\新建文件夹\\日志统计\\log\\筛选\\travel接口缓慢日志.log");
        FileUtil.writeUtf8Lines(consoleLines,"E:\\新建文件夹\\日志统计\\log\\筛选\\console接口缓慢日志.log");
        FileUtil.writeUtf8Lines(operateLines,"E:\\新建文件夹\\日志统计\\log\\筛选\\operator接口缓慢日志.log");
        //List<String> stringList = FileUtil.readLines("E:\\新建文件夹\\福建.txt", "utf-8");
        //for(int i=0;i<stringList.size();i++){
        //    String s = stringList.get(i);
        //    if(i==stringList.size()-1){
        //        System.out.println(s);
        //    }else{
        //        System.out.println(s+",");
        //    }
        //}
    }
}
