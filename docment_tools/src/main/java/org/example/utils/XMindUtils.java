package org.example.utils;

import java.io.IOException;
import org.xmind.core.Core;
import org.xmind.core.CoreException;
import org.xmind.core.ISheet;
import org.xmind.core.ITopic;
import org.xmind.core.IWorkbook;
import org.xmind.core.IWorkbookBuilder;

public class XMindUtils {
    private XMindUtils() {
    }

    /**
     * 通过文件路径获取根节点
     * @param path
     * @return
     */
    public static ITopic readRootTopic(String path) {
        IWorkbookBuilder builder = Core.getWorkbookBuilder();//初始化builder
        IWorkbook workbook = null;
        try {
            workbook = builder.loadFromPath(path);//打开XMind文件
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        if (workbook == null) {
            return null;
        }

        ITopic rootTopic = null;//获取根Topic
        try {
            ISheet defSheet = workbook.getPrimarySheet();//获取主Sheet
            rootTopic = defSheet.getRootTopic();
        } finally {
            if (rootTopic != null) {
                workbook.cloneTopic(rootTopic);
            }
        }
        return rootTopic;
    }
}
