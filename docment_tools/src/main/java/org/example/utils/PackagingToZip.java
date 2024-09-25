package org.example.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.codehaus.plexus.util.StringUtils;

public class PackagingToZip {
    /**
     *
     * @param inputFileName	要压缩的文件夹
     * @param zipFileName	压缩至目标文件夹
     * @return	Boolean
     * @throws Exception
     */
    public static Boolean zip(File inputFileName, String zipFileName) throws Exception {
        zip(zipFileName, inputFileName);
        return true;
    }

    private static void zip(String zipFileName, File inputFile) throws Exception {
        File file = new File(zipFileName);
        if(file.exists()){
            file.createNewFile();
        }
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(out, inputFile, "");
        out.flush();
        out.close();
    }

    private static void zip(ZipOutputStream out, File f, String base) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            if (StringUtils.isNotEmpty(base)){
                out.putNextEntry(new ZipEntry(base + "/"));
            }
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            in.close();
        }
    }
}
