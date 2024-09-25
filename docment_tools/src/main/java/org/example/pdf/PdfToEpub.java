package org.example.pdf;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.example.utils.EpubUtils;

public class PdfToEpub {
    /**
     * pdf转epub
     * @param path pdf文件
     * @param output 输出地址
     * 图片类型 jpg/png
     */
    public static void pdfToEpub(String path, String output) {
        //Path picDir = new File(String.format("/%s/%s",output, UUID.randomUUID())).toPath();
        Path picDir = new File("D:\\新建文件夹\\jpg\\32500fe4-ce53-41d8-a40e-84b9e1a224e0").toPath();
        try {
            String name = path.substring(path.lastIndexOf("\\"), path.lastIndexOf("."));
            Files.createDirectories(picDir);
            PdfToImage.pdf2png(path,picDir.toAbsolutePath().toString(),"png");
            EpubUtils epubUtils = new EpubUtils();
            epubUtils.create(name+".epub",picDir.toFile(),output);
            //FileUtils.deleteDirectory(picDir.toFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        pdfToEpub("D:\\新建文件夹\\宏观经济学 第十版.pdf","D:\\新建文件夹\\jpg");
    }
}
