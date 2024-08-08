package org.example.pdf;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

public class PdfSplitAtPage {
    public static void main(String[] args) throws IOException {
        File file = new File("E:\\新建文件夹\\计算机网络.pdf");

        // load pdf file
        PDDocument document = Loader.loadPDF(file);

        // instantiating Splitter
        Splitter splitter = new Splitter();

        splitter.setSplitAtPage(134);

        // split the pages of a PDF document
        List<PDDocument> Pages = splitter.split(document);

        // Creating an iterator
        Iterator<PDDocument> iterator = Pages.listIterator();

        // saving splits as pdf
        int i = 0;
        while(iterator.hasNext()) {
            PDDocument pd = iterator.next();
            pd.save("E:\\新建文件夹\\"+ ++i +".pdf");
            System.out.println("E:\\新建文件夹\\"+ i +".pdf");
        }

        // close the document
        document.close();
    }
}
