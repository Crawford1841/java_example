package org.example.socket.nio.chat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

//聊天程序客户端
public class NioChatSocketClient {
    private final String HOST="192.168.0.46";
    private int PORT = 9999;
    private SocketChannel socketChannel;//网络通道
    private String userName;

    public NioChatSocketClient()throws Exception{
        //1.得到一个网络通道
        socketChannel = SocketChannel.open();
        //2.设置非阻塞方式
        socketChannel.configureBlocking(false);
        //3.提供服务器端的IP地址和端口号
        InetSocketAddress address = new InetSocketAddress(HOST, PORT);
        //4. 连接服务器端
        if(!socketChannel.connect(address)){
            while (!socketChannel.finishConnect()){//nio作为非阻塞式的优势
                System.out.println("Client：连接服务器端的同时，可以去处理其它业务");

            }
        }
        //5、得到客户端IP地址和端口信息，作为聊天用户名使用
        userName = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println("-------------client("+userName+") is ready------------");
    }
    //向服务器发送数据
    public void sendMsg(String msg)throws Exception{
        if(msg.equalsIgnoreCase("bye")){
            socketChannel.close();
        }
        msg = userName+"说："+msg;
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(buffer);
    }

    //从服务器端接受数据
    private void receiveMsg()throws Exception{
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int size = socketChannel.read(buffer);
        if(size>0){
            String msg = new String(buffer.array());
            System.out.println(msg.trim());
        }
    }


    public static void main(String[] args) throws Exception{
        NioChatSocketClient chatSocketClient = new NioChatSocketClient();
        new Thread(()->{
            //监听服务器消息
            while (true){
                try {
                    chatSocketClient.receiveMsg();
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String message = scanner.nextLine();
            chatSocketClient.sendMsg(message);
        }




    }
}
