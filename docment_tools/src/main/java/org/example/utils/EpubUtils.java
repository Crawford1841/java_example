package org.example.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.UUID;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.format.DateTimeFormat;

public class EpubUtils {
    private String uuid;
    private File basedir;
    private ClassLoader classLoader;
    private String title;
    private File imgsDir;


    public void create(String title, File imgsDir, String output) throws IOException {
        this.title = title;
        this.imgsDir = imgsDir;
        basedir = this.mkdir(output);
        classLoader = getClass().getClassLoader();
        copyImages();
        copyStandardFilez();
        createOPFFile();
        createIndex();
        createTitlePage();
        createTOC();
        try {
            PackagingToZip.zip(basedir,output+"\\"+title);
        } catch (Exception e) {
            throw new RuntimeException("文件生成失败:"+e);
        }
        FileUtils.deleteDirectory(basedir);
        //FileUtils.deleteDirectory(targetFile);
    }

    private void copyImages() throws IOException {
        File imagesDir = new File(basedir, "images");
        imagesDir.mkdirs();
        for(File file : listFiles()){
            try(FileInputStream fileInputStream = new FileInputStream(file);
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(imagesDir, file.getName()))) {
                IOUtils.copy(fileInputStream, fileOutputStream);
            }
        }
    }

    private void copyStandardFilez() throws IOException {
        File metainf = new File(basedir, "META-INF");
        metainf.mkdirs();
        writeFile(new File(basedir, "mimetype"), readFileFromSrc("epub/mimetype"));
        writeFile(new File(metainf, "container.xml"), readFileFromSrc("epub/META-INF/container.xml"));
        writeFile(new File(basedir, "page_styles.css"), readFileFromSrc("epub/page_styles.css"));
        writeFile(new File(basedir, "stylesheet.css"), readFileFromSrc("epub/stylesheet.css"));
    }

    private void createOPFFile() throws IOException {
        StringBuilder content = new StringBuilder();
        for(File file : listFiles()){
            content.append(String.format("<item href=\"images/%s\" id=\"%s\" media-type=\"image/png\"/>\n", file.getName(), idForImage(file.getName())));
        }
        String opf = readFileFromSrc("epub/content.opf");
        String timestamp = DateTimeFormat.forPattern("yyyy-MM-dd'T'hh:mm:ssSZZ").print(DateTime.now().getTime());
        opf = opf
                .replace("$AUTHOR", "Unknown")
                .replace("$TIMESTAMP", timestamp)
                .replace("$TITLE", title)
                .replace("$UUID", uuid)
                .replace("$CONTENT", content.toString());

        writeFile(new File(basedir, "content.opf"), opf);
    }

    private void createIndex() throws IOException {
        StringBuilder content = new StringBuilder();
        for(File file : listFiles()){
            content.append(String.format("<p class=\"pdf-converter1\"><a id=\"%s\"></a><img src=\"images/%s\" class=\"pdf-converter2\"/></p>\n", idForImage(file.getName()), file.getName()));
        }
        String index = readFileFromSrc("epub/index.html");
        index = index.replace("$CONTENT", content.toString());

        writeFile(new File(basedir, "index.html"), index);
    }

    private void createTitlePage() throws IOException {
        String pagetitle = readFileFromSrc("epub/titlepage.xhtml");
        pagetitle = pagetitle.replace("$TITLE", title);

        writeFile(new File(basedir, "titlepage.xhtml"), pagetitle);
    }

    private void createTOC() throws IOException {
        String toc = readFileFromSrc("epub/toc.ncx");
        toc = toc.replace("$TITLE", title);

        writeFile(new File(basedir, "toc.ncx"), toc);
    }

    private void writeFile(File dest, String content) throws IOException {
        FileWriter writer = new FileWriter(dest);
        writer.write(content);
        writer.flush();
        writer.close();
    }

    private String readFileFromSrc(String path) throws IOException {
        return IOUtils.toString(classLoader.getResourceAsStream(path));
    }

    private String idForImage(String name){
        return String.format("id%s", name.replace(".png", ""));
    }

    private List<File> listFiles(){
        File[] files = imgsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        List<File> sorted = Arrays.asList(files);
        sorted.sort((a,b)->{
            String aName = a.getName().substring(0, a.getName().indexOf("."));
            String bName = b.getName().substring(0, b.getName().indexOf("."));
            int i1 = Integer.parseInt(aName);
            int i2 = Integer.parseInt(bName);
            return i1-i2;
        });
        return sorted;
    }

    public static Long getCRC32(String fileUri) {
        CRC32 crc32 = new CRC32();
        FileInputStream fileinputstream = null;
        CheckedInputStream checkedinputstream = null;
        Long crc = null;
        try {
            fileinputstream = new FileInputStream(new File(fileUri));
            checkedinputstream = new CheckedInputStream(fileinputstream, crc32);
            while (checkedinputstream.read() != -1) {
            }
            crc = crc32.getValue();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileinputstream != null) {
                try {
                    fileinputstream.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (checkedinputstream != null) {
                try {
                    checkedinputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return crc;
    }

    private File mkdir(String pathTemp){
        uuid = UUID.randomUUID().toString();
        File basedir = new File(pathTemp+"\\"+uuid);
        basedir.delete();
        basedir.mkdirs();
        return basedir;

    }
}
