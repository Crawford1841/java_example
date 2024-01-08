package org.example.socket.nio.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * NIO 实现一个多人聊天案例
 */
public class NioChatSocketServer {
    //监听通道
    private ServerSocketChannel listenerChannel;
    //选择器对象
    private Selector selector;
    //服务器端口
    private static final int PROT = 9999;

    public NioChatSocketServer() {
        try {
            //1、开启Socket监听通道
            listenerChannel = ServerSocketChannel.open();
            //2、开启选择器
            selector = Selector.open();
            listenerChannel.bind(new InetSocketAddress(PROT));
            //4、设置为非阻塞模型
            listenerChannel.configureBlocking(false);
            //5. 将选择器绑定到监听通道并监听accpet事件
            listenerChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("真人网络聊天室 启动..........");
            System.out.println("真人网络聊天室 初始化端口 9999..........");
            System.out.println("真人网络聊天室 初始化网络ip地址..........");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void start()throws Exception{
        try {
            while (true){//不停的监控
                if(selector.select(2000)==0){
                    System.out.println("Server：没有客户端连接");
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isAcceptable()){//连接事件
                        SocketChannel accept = listenerChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector,SelectionKey.OP_READ);
                        System.out.println(accept.getRemoteAddress().toString().substring(1)+"上线了......");

                    }
                    if(key.isReadable()){//读取数据事件
                        readMsg(key);
                    }
                    iterator.remove();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //读取客户端发来的消息并广播出去
    public void readMsg(SelectionKey key)throws Exception{
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int count = channel.read(buffer);
        if(count>0){
            String msg = new String(buffer.array());
            printInfo(msg);
            //全员广播消息
            broadCast(channel,msg);
        }
    }

    //给所有客户端发消息
    public void broadCast(SocketChannel except, String msg) throws IOException {
        System.out.println("服务器广播消息了......");
        for(SelectionKey key:selector.keys()){
            Channel targetChannel = key.channel();
            if(targetChannel instanceof SocketChannel && targetChannel!=except){
                SocketChannel destChannel = (SocketChannel) targetChannel;
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                destChannel.write(buffer);
            }
        }
    }

    private void printInfo(String str) { //往控制台打印消息
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("[" + sdf.format(new Date()) + "] -> " + str);
    }

    public static void main(String[] args)throws Exception {
        /**
         * 指定jar包中的某一个类启动
         * java -cp java_case-1.0-SNAPSHOT.jar org.example.socket.nio.chat.NioChatSocketServer
         */
        new NioChatSocketServer().start();
    }

}
