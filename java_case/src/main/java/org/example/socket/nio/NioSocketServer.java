package org.example.socket.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NioSocketServer {
    public static void main(String[] args)throws Exception {
        //1.开启一个ServerSocketChannel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //2、开启一个Selector选择器
        Selector selector = Selector.open();
        //3、绑定9999端口
        System.out.println("服务器端启动");
        System.out.println("服务器初始化端口：9999");
        serverSocketChannel.bind(new InetSocketAddress(9999));
        //4、配置非阻塞方式
        serverSocketChannel.configureBlocking(false);
//        5、Selector选择器注册ServerSocketChannel通道，绑定连接操作
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6、循环执行：监听连接事件及读取数据操作
        while (true){
            //6.1 监控客户端连接
            //selecto.select()方法返回的是客户端的通道数，如果为0，则说明没有客户端连接。
            //nio非阻塞式的优势
            if(selector.select(2000)==0){
                System.out.println("Server：客户端暂时无连接，处理其它业务");
                continue;
            }
            //6.2 得到SelectionKey，判断通道里的事件
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            //遍历所有SelectionKey
            while (keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();
                //客户端先连接上，处理事件，然后客户端会向服务端发消息，再读取客户端的数据事件
                if(key.isAcceptable()){//客户端连接请求事件
                    System.out.println("OP_ACCEPT");
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    //注册通道，将通道交给selector选择器进行监控
                    //参数01-选择器
                    //参数02-服务器要监控读事件，客户端发送send数据，服务端read数据
                    //参数03-客户端传过来的数据要放在缓冲区
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                if(key.isReadable()){//读取客户端数据事件
                    //数据在通道中，先得到通道
                    SocketChannel channel = (SocketChannel) key.channel();
                    //取到一个缓冲区,nio读写数据都是基于缓冲区
                    ByteBuffer readBuffer = (ByteBuffer) key.attachment();
                    //从通道中将客户端发来的数据读到缓冲区
                    channel.read(readBuffer);
                    System.out.println(new String(readBuffer.array()));
//                    Charset charset = StandardCharsets.UTF_8;
//                    String receivedData = charset.decode(readBuffer).toString();
//                    System.out.println("客户端发送过来的数据："+receivedData);
                }
                //6.3 手动从集合中移除当前key，防止重复处理
                keyIterator.remove();
            }
        }
    }
//    public static void main(String[] args)throws Exception {
//        //1.开启一个ServerSocketChannel通道
//        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//        //2、开启一个Selector选择器
//        Selector selector = Selector.open();
//        //3、绑定9999端口
//        System.out.println("服务器端启动");
//        System.out.println("服务器初始化端口：9999");
//        serverSocketChannel.bind(new InetSocketAddress(9999));
//        //4、配置非阻塞方式
//        serverSocketChannel.configureBlocking(false);
////        5、Selector选择器注册ServerSocketChannel通道，绑定连接操作
//        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
//        //6、循环执行：监听连接事件及读取数据操作
//        while (true){
//            //6.1 监控客户端连接
//            //selecto.select()方法返回的是客户端的通道数，如果为0，则说明没有客户端连接。
//            //nio非阻塞式的优势
//            if(selector.select(2000)==0){
//                System.out.println("Server：客户端暂时无连接，处理其它业务");
//                continue;
//            }
//            //6.2 得到SelectionKey，判断通道里的事件
//            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
//            //遍历所有SelectionKey
//            while (keyIterator.hasNext()){
//                SelectionKey key = keyIterator.next();
//                //客户端先连接上，处理事件，然后客户端会向服务端发消息，再读取客户端的数据事件
//                if(key.isAcceptable()){//客户端连接请求事件
//                    System.out.println("OP_ACCEPT");
//                    SocketChannel socketChannel = serverSocketChannel.accept();
//                    socketChannel.configureBlocking(false);
//                    //注册通道，将通道交给selector选择器进行监控
//                    //参数01-选择器
//                    //参数02-服务器要监控读事件，客户端发送send数据，服务端read数据
//                    //参数03-客户端传过来的数据要放在缓冲区
//                    ByteBuffer buffer = ByteBuffer.allocate(1024);
//                    socketChannel.register(selector,SelectionKey.OP_READ, buffer.duplicate());
//                }
//                if(key.isReadable()){//读取客户端数据事件
//                    //数据在通道中，先得到通道
//                    SocketChannel channel = (SocketChannel) key.channel();
//                    //取到一个缓冲区,nio读写数据都是基于缓冲区
//                    ByteBuffer readBuffer = (ByteBuffer) key.attachment();
//                    //从通道中将客户端发来的数据读到缓冲区
//                    Integer bytesRead = channel.read(readBuffer);
//                    if(bytesRead>0){
//                        readBuffer.flip();
//                        Charset charset = StandardCharsets.UTF_8;
//                        String receivedData = charset.decode(readBuffer).toString();
//                        System.out.println("客户端发送过来的数据："+receivedData);
//                    }
//                }
//                //6.3 手动从集合中移除当前key，防止重复处理
//                keyIterator.remove();
//            }
//        }
//    }
}
