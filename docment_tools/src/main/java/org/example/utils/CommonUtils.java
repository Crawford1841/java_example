package org.example.utils;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommonUtils {
    public static void sort(List<File> collect){
        Collections.sort(collect, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                System.out.println("name："+o1.getName()+"，"+o2.getName());
                System.out.println(o1.getName().compareTo(o2.getName()));
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
}
