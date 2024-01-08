package org.example.socket.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

/**
 * NIO 和 BIO 有着相同的目的和作用，但是它们的实现方式完全不同；
 * BIO 以流的方式处理数据，而 NIO 以块的方式处理数据，块 IO 的效率比流 IO 高很多。
 * NIO 是非阻塞式的，这一点跟 BIO 也很不相同，使用它可以提供非阻塞式的高伸缩性网络。
 * NIO 主要有三大核心部分：
 * Channel通道
 * Buffer缓冲区
 * Selector选择器
 * 传统的 BIO 基于字节流和字符流进行操作，而 NIO 基于 Channel和 Buffer进行操作，数据总是从通道
 * 读取到缓冲区中，或者从缓冲区写入到通道中。Selector用于监听多个通道的事件（比如：连接请求，
 * 数据到达等），因此使用单个线程就可以监听多个客户端通道。
 */
public class NioFileWirte {
    public static void main(String[] args)throws Exception {
        writeTxt();
        readTxt();
        BioToCopyFile();
        NioToCopyFile();
    }

    /**
     * 写入文件
     * @throws Exception
     */
    public static void writeTxt()throws Exception{
        //1、创造输出流
        FileOutputStream fos = new FileOutputStream("D:\\workspace\\java_example\\java_case\\src\\main\\resources\\basic.txt");
        //2、从流中得到一个通道
        FileChannel fc = fos.getChannel();
        //3、提供一个缓存区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //4、往缓冲区存入数据
        Scanner sc = new Scanner(System.in);
        buffer.put(sc.nextLine().getBytes());
        //5、翻转缓冲区
        buffer.flip();
        //6、把缓冲区写到通道中
        fc.write(buffer);
        fos.close();
    }

    /**
     * 读取文件
     * @throws Exception
     */
    public static void readTxt()throws Exception{
        File file = new File("D:\\workspace\\java_example\\java_case\\src\\main\\resources\\basic.txt");
        //1、 创建输入流
        FileInputStream fis = new FileInputStream(file);
        //2、得到一个通道
        FileChannel fc = fis.getChannel();
        //3、准备一个缓冲区
        ByteBuffer buffer = ByteBuffer.allocate((int)file.length());
        fc.read(buffer);
        byte[] array = buffer.array();
        String readLine = new String(array);
        System.out.println(readLine);
        fis.close();
    }

    /**
     * BIO复制文件
     * @throws Exception
     */
    public static void BioToCopyFile()throws Exception{
        FileInputStream fis = new FileInputStream("D:\\workspace\\java_example\\java_case\\src\\main\\resources\\basic.txt");
        FileOutputStream fos = new FileOutputStream("D:\\workspace\\java_example\\java_case\\src\\main\\resources\\basic_copy_bio.txt");
        byte[] b =new byte[1024];
        while (true){
            int res = fis.read(b);
            if(res==-1){
                break;
            }
            fos.write(b,0,res);
        }
        fis.close();
        fos.close();
    }

    /**
     * Nio复制文件
     * @throws Exception
     */
    public static void NioToCopyFile()throws Exception{
        //1、创建两个流对象
        FileInputStream fis = new FileInputStream("D:\\workspace\\java_example\\java_case\\src\\main\\resources\\basic.txt");
        FileOutputStream fos = new FileOutputStream("D:\\workspace\\java_example\\java_case\\src\\main\\resources\\basic_copy_nio.txt");
        //2、得到两个通道
        FileChannel sourceFileCopy = fis.getChannel();
        FileChannel destFileCopy = fos.getChannel();
        //3、复制
        destFileCopy.transferFrom(sourceFileCopy,0,sourceFileCopy.size());
        //4、关闭
        fis.close();
        fos.close();
    }

}
