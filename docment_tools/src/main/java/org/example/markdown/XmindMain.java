package org.example.markdown;

import java.io.IOException;
import org.example.utils.ToMdUtils;

/**
 * 思维导图转Markdown文档
 */
public class XmindMain {
    public static void main(String[] args) throws IOException {
        // xmind
        //ToMdUtils.toMD(
        //        "D:\\workspace\\my_github\\java_example\\docment_tools\\src\\main\\resources\\test.xmind",
        //        i -> System.out.print(i.toString())
        //);

        // pos
        ToMdUtils.toMD(
                "D:\\workspace\\my_github\\java_example\\docment_tools\\src\\main\\resources\\思维导图.pos",
                i -> System.out.print(i.toString())
        );
    }
}
