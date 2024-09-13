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
import java.util.stream.Collectors;

public class AnalysisLog {
    static Map<String,List<Log>> travelLine = new HashMap<>(1000);
    static Map<String,List<Log>> operateLine = new HashMap<>(1000);
    static Map<String,List<Log>> consoleLine = new HashMap<>(1000);
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
                //System.out.println("接口路径："+url);
                //System.out.println("接口参数："+parms);
                //System.out.println("响应时间: " + matcher.group());
                //System.out.println("日志详情："+log);

                //响应时间
                Integer newMs = Integer.valueOf(matcher.group().replaceAll("ms", "").trim());
                StringBuilder sb = new StringBuilder();
                sb.append("接口："+url+"，");
                sb.append("参数："+parms+"，");
                sb.append("响应时间："+newMs+"ms");
                if(url.indexOf("/travel")>0){
                    put(travelLine,newMs,sb.toString(),url,parms);
                }else if(url.indexOf("/console")>0){
                    put(consoleLine,newMs,sb.toString(),url,parms);
                }else{
                    put(operateLine,newMs,sb.toString(),url,parms);
                }
                //if(newMs>100){
                //    StringBuilder sb = new StringBuilder();
                //    sb.append("接口："+url+"，");
                //    sb.append("参数："+parms+"，");
                //    sb.append("响应时间："+newMs+"ms");
                //    if(url.indexOf("/travel")>0){
                //        put(travelLine,newMs,sb.toString(),url);
                //    }else if(url.indexOf("/console")>0){
                //        put(consoleLine,newMs,sb.toString(),url);
                //    }else{
                //        put(operateLine,newMs,sb.toString(),url);
                //    }
                //}
            }
        });
    }

    private static void put(Map<String,List<Log>> logLine,int newMs,String content,String url,String params){
        Log data = new Log();
        data.setMs(newMs);
        data.setContent(content);
        data.setParams(params);
        if(logLine.containsKey(url)){
            List<Log> l = logLine.get(url);
            System.out.println(url+"，数量："+logLine.size());
            l.add(data);
        }else{
            List<Log> l = new ArrayList<>();
            l.add(data);
            logLine.put(url,l);
        }
    }

    public static void main(String[] args) {
        List<File> list = FileUtil.loopFiles("E:\\新建文件夹\\新建文件夹\\2024-09-04.log");
        list.forEach(item->{
            List<String> strings = FileUtil.readUtf8Lines(item);
            find(strings);
        });
        //接口名称：[]，接口请求次数：[]，总耗时：[]，中位数耗时：[]，平均耗时：[]，最小耗时：[]，最大耗时：[]，最小耗时请求参数：[]，最大耗时请求参数：[]
        List<String> travelLines = new ArrayList<>();
        travelLine.entrySet().forEach((item)->{
            StringBuilder builder = new StringBuilder();
            List<Log> value = item.getValue();
            value.sort((a,b)->a.getMs().compareTo(b.getMs()));

            String interfaceName = item.getKey();
            int count = value.size();
            int sumTime = value.stream().collect(Collectors.summingInt(Log::getMs));
            int medianTime = value.get(count / 2).getMs();
            int avgTime = value.stream().collect(Collectors.averagingInt(Log::getMs)).intValue();
            int minTime = value.get(0).getMs();
            int maxTime = value.get(count-1).getMs();
            String minContexnt = value.get(0).getContent();
            String maxContenxt = value.get(count-1).getContent();
            builder.append("接口名称：["+interfaceName+"]，接口请求次数：["+count+"]，总耗时：["+sumTime+"]，中位数耗时：["+medianTime+"]，平均耗时：["+avgTime+"]，最小耗时：["+minTime+"]，最大耗时：["+maxTime+"]，最小耗时请求参数：["+minContexnt+"]，最大耗时请求参数：["+maxContenxt+"]\n");
            System.out.println(builder);
            travelLines.add(builder.toString());
        });
        FileUtil.writeUtf8Lines(travelLines,"E:\\新建文件夹\\日志统计\\log\\筛选\\travel接口缓慢日志.log");



        //List<Log> travel = travelLine.entrySet().stream().map(item -> {
        //    return item.getValue();
        //}).collect(Collectors.toList());
        //travel.sort((a,b)->a.getMs().compareTo(b.getMs()));
        //List<String> travelLines = travel.stream().map(Log::getContent).collect(Collectors.toList());
        //
        //
        //List<Log> console = consoleLine.entrySet().stream().map(item -> {
        //    return item.getValue();
        //}).collect(Collectors.toList());
        //console.sort((a,b)->a.getMs().compareTo(b.getMs()));
        //List<String> consoleLines = console.stream().map(Log::getContent).collect(Collectors.toList());
        //
        //List<Log> operate = operateLine.entrySet().stream().map(item -> {
        //    return item.getValue();
        //}).collect(Collectors.toList());
        //operate.sort((a,b)->a.getMs().compareTo(b.getMs()));
        //
        //List<String> operateLines = operate.stream().map(Log::getContent).collect(Collectors.toList());
        //
        //
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
