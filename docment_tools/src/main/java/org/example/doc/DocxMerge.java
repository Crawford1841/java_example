package org.example.doc;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/7/12 22:51
 */

import cn.hutool.core.io.FileUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class DocxMerge {
    private static final String outPath = "D:\\merge.docx";
    public static void main(String[] args) {
        List<File> files = FileUtil.loopFiles(new File("D:\\application\\MemoTrace\\data\\聊天记录\\釒融大佬吹水(18727965991@chatroom)"),1,null);
        List<File> list = files.stream().filter(item -> item.getName().contains(".docx")).sorted().collect(Collectors.toList());
        list.sort((o1,o2)->{
            String name = o1.getName().substring(o1.getName().lastIndexOf("_")+1,o1.getName().lastIndexOf("."));
            String _name = o2.getName().substring(o2.getName().lastIndexOf("_")+1,o2.getName().lastIndexOf("."));
            Integer a = Integer.parseInt(name);
            Integer b = Integer.parseInt(_name);
            return a.compareTo(b);
        });

        appendDocx(outPath, list);
    }


    /**
     * 把多个docx文件合并成一个
     *
     * @param outPath    输出文件
     * @param targetFile 目标文件
     */
    public static void appendDocx(String outPath, List<File> targetFile) {
        try {
            File outfile = new File(outPath);
            OutputStream dest = new FileOutputStream(outfile);
            ArrayList<XWPFDocument> documentList = new ArrayList<>();
            XWPFDocument doc = null;
            for (int i = 0; i < targetFile.size(); i++) {
                FileInputStream in = new FileInputStream(targetFile.get(i).getPath());
                OPCPackage open = OPCPackage.open(in);
                XWPFDocument document = new XWPFDocument(open);
                documentList.add(document);
            }
            for (int i = 0; i < documentList.size(); i++) {
                doc = documentList.get(0);
                if (i != 0) {
                    //解决word合并完后，所有表格都紧紧挨在一起，没有分页。加上了分页符可解决
                    documentList.get(i).createParagraph().setPageBreak(true);
                    appendBody(doc, documentList.get(i));
                }
            }

            doc.write(dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void appendBody(XWPFDocument src, XWPFDocument append) throws Exception {
        CTBody src1Body = src.getDocument().getBody();
        CTBody src2Body = append.getDocument().getBody();

        List<XWPFPictureData> allPictures = append.getAllPictures();
        // 记录图片合并前及合并后的ID
        Map<String, String> map = new HashMap<>();
        for (XWPFPictureData picture : allPictures) {
            String before = append.getRelationId(picture);
            //将原文档中的图片加入到目标文档中
            String after = src.addPictureData(picture.getData(), Document.PICTURE_TYPE_PNG);
            map.put(before, after);
        }

        appendBody(src1Body, src2Body, map);

    }

    private static void appendBody(CTBody src, CTBody append, Map<String, String> map) throws Exception {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = append.xmlText(optionsOuter);

        String srcString = src.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        String sufix = srcString.substring(srcString.lastIndexOf("<"));
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));
        //下面这部分可以去掉，我加上的原因是合并的时候，有时候出现打不开的情况，对照document.xml将某些标签去掉就可以正常打开了
        addPart = addPart.replaceAll("w14:paraId=\"[A-Za-z0-9]{1,10}\"", "");
        addPart = addPart.replaceAll("w14:textId=\"[A-Za-z0-9]{1,10}\"", "");
        addPart = addPart.replaceAll("w:rsidP=\"[A-Za-z0-9]{1,10}\"", "");
        addPart = addPart.replaceAll("w:rsidRPr=\"[A-Za-z0-9]{1,10}\"", "");
        addPart = addPart.replace("<w:headerReference r:id=\"rId8\" w:type=\"default\"/>","");
        addPart = addPart.replace("<w:footerReference r:id=\"rId9\" w:type=\"default\"/>","");
        addPart = addPart.replace("xsi:nil=\"true\"","");

        if (map != null && !map.isEmpty()) {
            //对xml字符串中图片ID进行替换
            for (Map.Entry<String, String> set : map.entrySet()) {
                addPart = addPart.replace(set.getKey(), set.getValue());
            }
        }
        //将两个文档的xml内容进行拼接
        CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);

        src.set(makeBody);
    }
}
