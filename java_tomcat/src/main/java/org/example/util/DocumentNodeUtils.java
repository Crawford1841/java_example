package org.example.util;

import java.io.InputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.example.server.Server;

/**
 * 节点读取工具
 */
public class DocumentNodeUtils {

    public static Object readNode(String name) throws DocumentException {
        //初始化端口，读取配置文件server.xml中的端口号
        InputStream in =  Server.class.getClassLoader().getResourceAsStream("server.xml");
        //读取配置文件输入流
        SAXReader saxReader = new SAXReader();
        Document doc = saxReader.read(in);
        //使用SAXReader + XPath读取端口配置
        Element portEle = (Element) doc.selectSingleNode(name);

        return Integer.valueOf(portEle.getText());
    }
}
