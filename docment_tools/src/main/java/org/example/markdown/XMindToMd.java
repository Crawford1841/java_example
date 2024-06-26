package org.example.markdown;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;
import org.codehaus.plexus.util.StringUtils;
import org.example.utils.XMindUtils;
import org.example.utils.ZipUtils;
import org.example.markdown.core.AbstractToMD;
import org.xmind.core.ITopic;

/**
 * 工具类
 *
 * @author yangchao
 */
public class XMindToMd extends AbstractToMD implements ToMdInterface {

    private static final XMindToMd TO_MD_UTILS = new XMindToMd();

    private XMindToMd() {
    }

    public static XMindToMd getInstance() {
        return TO_MD_UTILS;
    }

    /**
     * @param filePath              .xmind 文件位置
     * @param stringBuilderConsumer 处理响应的函数 （注意会多次调用）
     * @throws IOException
     */
    @Override
    public void toMD(String filePath, Consumer<StringBuilder> stringBuilderConsumer) throws IOException {
        File file = new File(filePath);
        String unPath = ZipUtils.unZipFiles(file, file.getParentFile().getPath() + "/");
        try {
            ITopic iTopic = XMindUtils.readRootTopic(unPath);
            if (iTopic == null) {
                System.out.println("Read error.");
                return;
            }

            iTopicToString(iTopic, 0, stringBuilderConsumer);
        } finally {
            deleteFile(new File(unPath));
        }
    }

    /**
     * 先根遍历序递归删除文件夹
     *
     * @param dirFile 要被删除的文件或者目录
     * @return 删除成功返回true, 否则返回false
     */
    private static boolean deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }

        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {

            for (File file : dirFile.listFiles()) {
                deleteFile(file);
            }
        }

        return dirFile.delete();
    }

    private void iTopicToString(ITopic iTopic, int level, Consumer<StringBuilder> consumer) {
        String titleText = iTopic.getTitleText();
        if (StringUtils.isNotBlank(titleText.trim())) {
            // 获得MD语法
            GetTop get = new GetTop(level, titleText).invoke();

            // 处理超连接
            String hyperlink = iTopic.getHyperlink();
            setLink(hyperlink, get);

            StringBuilder str = new StringBuilder();
            // 处理标题
            addThis(get, str, level);

            // 处理 Labels
            Set<String> labels = iTopic.getLabels();
            addLabels(labels, get, str);

            // 处理图片
            String source = iTopic.getImage().getSource();
            addImage(source, str);

            // 调用处理函数
            consumer.accept(str);
        }
        // 递归
        iTopic.getAllChildren().forEach(i -> iTopicToString(i, level + 1, consumer));
    }
}
