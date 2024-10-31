package org.example.pdf;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/4/27 17:10
 */

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.example.utils.CommonUtils;

public class PdfMergeTools {
    /**
     * pdf合并拼接
     * @Title:mulFile2One
     * @Description: TODO
     * @date 2019年9月22日 上午10:05:37
     * @author yqwang
     * @param files 文件列表
     * @param targetPath 合并到
     * @return
     * @throws IOException
     */
    public static File mulFile2One(List<File> files,String targetPath) throws IOException{
        // pdf合并工具类
        PDFMergerUtility mergePdf = new PDFMergerUtility();
        for (File f : files) {
            if(f.exists() && f.isFile()){
                // 循环添加要合并的pdf
                mergePdf.addSource(f);
            }
        }
        // 设置合并生成pdf文件名称
        mergePdf.setDestinationFileName(targetPath);
        // 合并pdf
        mergePdf.mergeDocuments(MemoryUsageSetting.setupTempFileOnly().streamCache);
        return new File(targetPath);
    }
    public static void main(String[] args) throws IOException {
//        File file = new File("D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\讲义");
//        File[] files = file.listFiles();
//        List<File> collect = Arrays.stream(files).collect(Collectors.toList());
//        sort(collect);
//        File f = mulFile2One(collect, "D:\\考试\\CPA笔记精选\\会计\\01-零基础预习班-张敬富（23讲全）\\讲义\\预习.pdf");

        File file = new File("E:\\新建文件夹\\156程序员的个人财富课");
        File[] files = file.listFiles();
        List<File> collect = Arrays.stream(files).collect(Collectors.toList());
        List<File> pdfs = collect.stream().filter(item -> {
            String suffix = item.getName().substring(item.getName().lastIndexOf(".")+1, item.getName().length());
            return "pdf".equals(suffix);
        }).collect(Collectors.toList());
        CommonUtils.sort(pdfs);
        File f = mulFile2One(pdfs, "E:\\新建文件夹\\pdf\\程序员的个人财富课.pdf");
        System.out.println(f.length());
    }

}
