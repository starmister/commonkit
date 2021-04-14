package com.songwy.utils.io.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class NioClient {
    private Logger logger = LoggerFactory.getLogger(NioClient.class);
    private SocketChannel clientSocketChannel;
    private Selector selector;
    private final List<String> responseQueue = new ArrayList<>();
    private CountDownLatch connected = new CountDownLatch(1);

    public NioClient() throws IOException, InterruptedException {
        //打开客户端连接通道
        clientSocketChannel = SocketChannel.open();
        //设置为非阻塞
        clientSocketChannel.configureBlocking(false);
        //创建选择器
        selector = Selector.open();
        //绑定channel到selector，注册连接事件
        clientSocketChannel.register(selector, SelectionKey.OP_CONNECT);
        //连接服务器
        clientSocketChannel.connect(new InetSocketAddress(8080));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handleKeys();
                }catch (IOException e) {
                  e.printStackTrace();
                }

            }
        }).start();

        if (connected.getCount() != 0) {
            connected.await();
        }
        logger.info("Client 启动完成");

    }

    private void handleKeys() throws IOException {
        while (true) {
            int selectNums = selector.select(30 * 1000L);
            if(selectNums == 0){
                continue;
            }

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
        //已连接
        if(key.isConnectable()) {
            handleConnectableKey(key);
        }
        //可写
        if(key.isWritable()) {
            handleWritableKey(key);
        }
        //可读
        if(key.isReadable()) {
            handleReadableKey(key);
        }
    }

    private void handleConnectableKey(SelectionKey key) throws IOException {
        //完成连接
        if (!clientSocketChannel.isConnectionPending()) {
            return ;
        }
        clientSocketChannel.finishConnect();
        logger.info("接受新的channel");
        clientSocketChannel.register(selector, SelectionKey.OP_READ, responseQueue);
        //标记为已连接
        connected.countDown();
    }

    private void handleReadableKey(SelectionKey key) throws ClosedChannelException {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = CodecUtil.read(clientSocketChannel);
        // 写入模式下，
        if (readBuffer.position() > 0) {
            String content = CodecUtil.newString(readBuffer);
            logger.info("读取数据：" + content);
        }
    }

    @SuppressWarnings("Duplicates")
    private void handleWritableKey(SelectionKey key) throws ClosedChannelException {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        List<String> responseQueue = (ArrayList<String>) key.attachment();
        for (String content : responseQueue) {
            CodecUtil.write(clientSocketChannel, content);
        }
        responseQueue.clear();
        clientSocketChannel.register(selector, SelectionKey.OP_READ, responseQueue);
    }

    public synchronized void send(String content) throws ClosedChannelException {
        //添加响应队列
        responseQueue.add(content);
        clientSocketChannel.register(selector, SelectionKey.OP_WRITE, responseQueue);

        // 调用 Selector#wakeup() 方法，唤醒 #handleKeys() 方法中，Selector#select(long timeout) 方法的阻塞等待。
        // 因为，在 Selector#select(long timeout) 方法的实现中，是以调用当时，对 SocketChannel 的感兴趣的事件 。
        // 所以，即使修改了对 SocketChannel 的感兴趣的事件，也不会结束 Selector#select(long timeout) 方法的阻塞等待。
        // 因此，需要进行唤醒操作。
        selector.wakeup();
    }

    public static void main(String[] args) throws Exception{
        NioClient client = new NioClient();
        for(int i = 0; i < 30; i++){
            client.send("nihao: " + i);
            Thread.sleep(1000L);
        }

    }
}
