package org.example.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import cn.hutool.core.io.FileUtil;
import org.example.markdown.PosToMd;
import org.example.markdown.ToMdInterface;
import org.example.markdown.XMindToMd;

public class ToMdUtils {

    private ToMdUtils() {
    }

    /**
     * 转 POS|Xmind MD
     * @param filePath
     * @param stringBuilderConsumer 输出到 stringBuilderConsumer （注意会多次调用）
     * @return
     *      @see org.example.markdown.ToMdInterface
     * @throws IOException
     */
    public static ToMdInterface toMD(String filePath, Consumer<StringBuilder> stringBuilderConsumer) throws IOException {
        // 获取后缀
        String[] split = filePath.split("\\.");
        String suffix = split[split.length - 1];

        // 获取具体实现
        ToMdInterface instance;
        if (suffix.equalsIgnoreCase("pos")) {
            instance = PosToMd.getInstance();
        } else {
            instance = XMindToMd.getInstance();
        }

        // 执行
        instance.toMD(filePath, stringBuilderConsumer);
        return instance;
    }

    public static void main(String[] args) {
        List<File> files = FileUtil.loopFiles("E:\\书籍\\经济\\入门读物");
        List<File> files2 = FileUtil.loopFiles("E:\\书籍\\经济\\拓展读物");
        List<File> files3 = FileUtil.loopFiles("E:\\书籍\\经济\\黄哥推荐");
        List<File> files4 = FileUtil.loopFiles("E:\\书籍\\经济\\Math老师");
        List<File> files5 = FileUtil.loopFiles("E:\\书籍\\经济\\国内");
        List<File> files6 = FileUtil.loopFiles("E:\\书籍\\经济\\金融史");
        List<File> files7 = FileUtil.loopFiles("E:\\书籍\\经济\\补充");
        files.addAll(files2);
        files.addAll(files3);
        files.addAll(files4);
        files.addAll(files5);
        files.addAll(files6);
        files.addAll(files7);
        Set<String> set = new HashSet<>();
        files.forEach(item-> {
            if(item.getName().contains("CFA")){
                return;
            }
            String name = item.getName().substring(0, item.getName().indexOf("."));
            set.add(name);
        });
        System.out.println(set.size());
        set.forEach(item-> System.out.println(item));
    }
}
