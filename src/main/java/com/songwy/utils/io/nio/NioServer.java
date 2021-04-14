package com.songwy.utils.io.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NioServer {
    private Logger logger = LoggerFactory.getLogger(NioServer.class);
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public NioServer() throws IOException {

        //1.打开ServerSocketChannel
        serverSocketChannel = ServerSocketChannel.open();
        //2.设置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //3.绑定server port
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        //创建Selector
        selector = Selector.open();
        //注册ServerSocketChannel到Selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("Server 启动完成");

        handleKeys();
    }

    private void handleKeys() throws IOException {
        while (true){
            //通过Selector选择Channel
            int selectNums = selector.select(30*1000L);
            if(selectNums == 0){
                continue;
            }
            logger.info("选择 Channel 数量：" + selectNums);
            //遍历可选择的Channel的SelectionKey集合
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                //移除这次处理的SelectionKey
                iterator.remove();
                //忽略无效的SelectionKey
                if(!key.isValid()){
                    continue;
                }
                handleKey(key);
            }
        }
    }

    private void handleKey(SelectionKey key) throws IOException {
        //接受连接请求
        if(key.isAcceptable()) {
            handleAcceptableKey(key);
        }
        //读就续
        if(key.isReadable()) {
            handleReadableKey(key);
        }
        //写就续
        if(key.isWritable()) {
            handleWritableKey(key);
        }
    }

    private void handleAcceptableKey(SelectionKey key) throws IOException {
        //接受SocketChannel
        SocketChannel clientSocketChannel = ((ServerSocketChannel) key.channel()).accept();
        //设置客户端通道为非阻塞
        clientSocketChannel.configureBlocking(false);
        logger.info("接受新的channel");
        //注册clientSocketChannel到Selector
        clientSocketChannel.register(selector, SelectionKey.OP_READ, new ArrayList<String>());
    }

    private void handleReadableKey(SelectionKey key) throws IOException {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = CodecUtil.read(clientSocketChannel);
        if(readBuffer == null){
            logger.info("连接断开");
            clientSocketChannel.register(selector,0);
            return;
        }
        //读取数据
        if(readBuffer.position() > 0) {
            String content = CodecUtil.newString(readBuffer);
            logger.info("读取数据：" + content);
            List<String> responseQueue = (ArrayList<String>) key.attachment();
            responseQueue.add("响应：" + content);
            //注册clientSocketChannel到Selector
            clientSocketChannel.register(selector, SelectionKey.OP_WRITE, key.attachment());
        }
    }

    private void handleWritableKey(SelectionKey key) throws ClosedChannelException {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        List<String> responseQueue = (ArrayList<String>) key.attachment();
        for (String content : responseQueue) {
            logger.info("写入数据：" + content);
            CodecUtil.write(clientSocketChannel, content);
        }
        responseQueue.clear();
        // 一般来说，你不应该注册写事件。
        // 写操作的就绪条件为底层缓冲区有空闲空间，而写缓冲区绝大部分时间都是有空闲空间的，所以当你注册写事件后，写操作一直是就绪的，选择处理线程全占用整个CPU资源。
        // 所以，只有当你确实有数据要写时再注册写操作，并在写完以后马上取消注册。
        clientSocketChannel.register(selector, SelectionKey.OP_READ, responseQueue);
        System.out.println(key.interestOps());

    }

    public static void main(String[] args) throws Exception {
        NioServer nioServer = new NioServer();

    }

}
