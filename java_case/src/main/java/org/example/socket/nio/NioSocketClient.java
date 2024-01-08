package org.example.socket.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * 非阻塞 IO 的实现，基于事件驱动，非常适用于服务器需要维持大量连接，但是数据交换量不大的情况，例如：Web服务器、RPC、即时通信
 *在 Java 中编写 Socket 服务器，通常有以下几种模式：
 * 一个客户端连接用一个线程
 * 优点：程序编写简单
 * 缺点：如果连接非常多，分配的线程也会非常多，服务器可能会因为资源耗尽而崩溃。
 * 把每个客户端连接交给一个拥有固定数量线程的连接池
 * 优点：程序编写相对简单， 可以处理大量的连接。
 * 缺点：线程的开销非常大，连接如果非常多，排队现象会比较严重。
 * 使用 Java 的 NIO，用非阻塞的 IO 方式处理
 * 优点：这种模式可以用一个线程，处理大量的客户端连接
 * 缺点：代码复杂度较高，不易理解
 */
public class NioSocketClient {
    /**
     * Selector选择器
     * 能够检测多个注册的通道上是否有事件发生（读、写、连接），如果有事件发生，便获取事件然后针对
     * 每个事件进行相应的处理。这样就可以只用一个单线程去管理多个通道，也就是管理多个连接。这样使
     * 得只有在连接真正有读写事件发生时，才会调用函数来进行读写，就大大地减少了系统开销，并且不必
     * 为每个连接都创建一个线程，不用去维护多个线程，并且避免了多线程之间的上下文切换导致的开销
     * 该类的常用方法如下所示：
     *      public static Selector open()，得到一个选择器对象
     *      public int select(long timeout)，监控所有注册的通道，当其中有 IO 操作可以进行时，将对应的
     *      SelectionKey 加入到内部集合中并返回，参数用来设置超时时间
     *      public Set selectedKeys()，从内部集合中得到所有的 SelectionKey
     */


    /**
     * SelectionKey
     * 代表了 Selector 和网络通道的注册关系
     * 一共四种（就是连接事件）
     * int OP_ACCEPT：有新的网络连接可以 accept，值为 16
     * int OP_CONNECT：代表连接已经建立，值为 8
     * int OP_READ 和 int OP_WRITE：代表了读、写操作，值为 1 和 4
     * 该类的常用方法如下所示：
     * public abstract Selector selector()，得到与之关联的 Selector 对象
     * public abstract SelectableChannel channel()，得到与之关联的通道
     * public final Object attachment()，得到与之关联的共享数据
     * public abstract SelectionKey interestOps(int ops)，设置或改变监听事件
     * public final boolean isAcceptable()，是否可以 accept
     * public final boolean isReadable()，是否可以读
     * public final boolean isWritable()，是否可以写
     */

    /**
     * ServerSocketChannel
     * 用来在服务器端监听新的客户端 Socket 连接
     * public static ServerSocketChannel open()，得到一个 ServerSocketChannel 通道
     * public final ServerSocketChannel bind(SocketAddress local)，设置服务器端端口号
     * public final SelectableChannel configureBlocking(boolean block)，设置阻塞或非阻塞模式， 取值 false 表示采用非阻塞模式
     * public SocketChannel accept()，接受一个连接，返回代表这个连接的通道对象
     * public final SelectionKey register(Selector sel, int ops)，注册一个选择器并设置监听事件
     *
     */

    /**
     * SocketChannel
     * 网络 IO 通道，具体负责进行读写操作NIO 总是把缓冲区的数据写入通道，或者把通道里的数据读到缓冲区
     * public static SocketChannel open()，得到一个 SocketChannel 通道
     * public final SelectableChannel configureBlocking(boolean block)，设置阻塞或非阻塞模式， 取值 false 表示采用非阻塞模式
     * public boolean connect(SocketAddress remote)，连接服务器
     * public boolean finishConnect()，如果上面的方法连接失败，接下来就要通过该方法完成连接操作
     * public int write(ByteBuffer src)，往通道里写数据
     * public int read(ByteBuffer dst)，从通道里读数据
     * public final SelectionKey register(Selector sel, int ops, Object att)，注册一个选择器并设置监听事件，最后一个参数可以设置共享数据
     * public final void close()，关闭通道
     *
     */

    public static void main(String[] args)throws Exception {
        //1、得到一个网络通道
        SocketChannel channel = SocketChannel.open();
        //2、设置非阻塞方式
        channel.configureBlocking(false);
        //3、提供服务器端的IP地址和端口号
        InetSocketAddress address = new InetSocketAddress("127.0.0.1",9999);
        //4、连接服务器端，如果用connect()方法连接服务器不成功，则用finishConnect()方法进行连接
        if(!channel.connect(address)){
            //因为连接需要花事件，所以用while一直区尝试连接。在连接服务端时还可以做别的事情，体现非阻塞
            while(!channel.finishConnect()){
            //nio作为非阻塞式的优势，如果服务器没有响应（不启动服务端)，客户端不会阻塞，最后会报错，客户端尝试链接服务器连不上
                System.out.println("Clinet：连接服务端的同时，还可以干别的一些事情");

            }
        }
        //5、得到一个缓冲区并存入数据
        String msg = "123asd";
        ByteBuffer writeBuf = ByteBuffer.wrap(msg.getBytes());
        //6、发送数据
        channel.write(writeBuf);
        //阻止客户端停止，否则服务端也会停止
        System.in.read();



    }



}
