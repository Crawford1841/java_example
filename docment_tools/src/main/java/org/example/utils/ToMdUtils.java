package org.example.utils;

import java.io.IOException;
import java.util.function.Consumer;
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
}
