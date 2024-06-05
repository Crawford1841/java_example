package org.example.conf;

import java.io.File;
import java.util.Objects;

public class Constatnt {
    public static final String [] suffix_text = {".txt",".srt"};
    public static final String [] suffix_mp3 = {".mp3"};
    public static final String [] suffix_mp4 = {".mkv",".mp4"};
    public static final String [] suffix_pdf = {".pdf"};

    public static boolean exist(String[] suffix, File file){
        if(Objects.isNull(file) || Objects.isNull(suffix)){
            return false;
        }
        String suffixName = file.getName().substring(file.getName().lastIndexOf("."));
        for(int i=0;i<suffix.length;i++){
            if(suffix[i].equals(suffixName)){
                return true;
            }
        }
        return false;
    }
}
