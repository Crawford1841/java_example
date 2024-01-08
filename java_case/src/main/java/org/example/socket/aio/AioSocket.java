package org.example.socket.aio;

/**
 * 引入异步通道的概念， 简化了程序编写，一个有效的请求才启动一个线
 * 程，它的特点是先由操作系统完成后，才通知服务端程序启动线程去处理，一般适用于连接数较多
 * 且连接时间较长的应用
 */
public class AioSocket {
}
