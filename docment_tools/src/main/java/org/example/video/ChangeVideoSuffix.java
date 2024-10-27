package org.example.video;

/*
 * @author huangwei
 * @emaill 1142488172@qq.com
 * @date 2024/10/27 23:41
 */

import java.io.File;
import java.util.Scanner;

public class ChangeVideoSuffix {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要修改文件后缀名的文件夹:");
        String path = sc.nextLine();
        System.out.println("请输入修改前的后缀名:");
        String from = sc.nextLine();
        System.out.println("请输入修改后的后缀名:");
        String to = sc.nextLine();

        reName(path, from, to);
        System.out.println("全部修改完成!!!");
    }

    public static void reName(String path, String from, String to) {
        File f = new File(path);
        File[] fs = f.listFiles();
        for(int i=0;i<fs.length;i++){
            File subFile = fs[i];
            // 如果文件是文件夹则递归调用批量更改文件后缀名的函数
            if (subFile.isDirectory()) {
                reName(subFile.getPath(), from, to);
            } else {
                String name = subFile.getName();
                if (name.endsWith(from)) {

//                    subFile.renameTo(new File(subFile.getParent() + "/" + name.substring(0, name.indexOf(from)) + to));
                    subFile.renameTo(new File(subFile.getParent() + "/" + "data_"+i+"." + to));
                    /*
                     * 可在Java API中的File类中查询renameTo的方法
                     * renameTo可以用来给File改名字,改路径
                     * 他需要的参数也是一个File对象,表示要把当前文件重命名(移动)为哪个文件
                     * 如果目标文件存在,则此方法返回false
                     *
                     * renameTo不会产生新文件,他只是把文件移动一下,或者改个名字
                     *
                     * 实际上,这个方法的具体表现与操作系统,和文件系统都有关系.
                     * 它不能把一个文件从一个文件系统移动到另一个文件系统,例如: 不能把c:\a.txt renameTo 为
                     * d:\a.txt 因为c: d:属于不同的盘(文件系统) 但可以把c:\a.txt renameTo
                     * c:\system\bb.txt (路径,文件名都可以变,但还是在同一个分区)
                     * linux,unix的分区也是同样的道理,只不过不像Windows这么明显一眼就看出来不是同一个分区
                     *
                     * 你可以在系统中试一下: 在同一个分区内,剪切一个文件 ,在粘贴到另一个位置,这是瞬间完成的,无论文件多么大.
                     * 实际上没有copy操作,java的renameTo就是这个意思 不同分区的话,那就得先复制,然后删除源文件
                     */
                }
            }
        }
    }

}
