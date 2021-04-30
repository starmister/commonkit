package com.songwy.utils.io.nio.reactor.multi;

import com.songwy.utils.io.nio.reactor.single.Handler;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MultiHandler implements Runnable {
    private volatile static Selector selector;
    private SocketChannel channel;
    private SelectionKey key;
    private volatile ByteBuffer input = ByteBuffer.allocate(1024);
    private volatile ByteBuffer output = ByteBuffer.allocate(1024);
    //多线程处理业务逻辑
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    public MultiHandler(SocketChannel channel,Selector selector) throws IOException {
        this.channel = channel;
        // 设置客户端连接为非阻塞模式
        channel.configureBlocking(false);
        // 为客户端创建一个新的多路复用器
        //selector = Selector.open();
        // 注册客户端Channel的读事件
        MultiHandler.selector = selector;
        key = channel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    public void run() {
        try {
            while (selector.isOpen() && channel.isOpen()) {
                // 等待客户端事件发生
                Set<SelectionKey> keys = select();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    // 如果当前是读事件，则读取数据
                    if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        // 如果当前是写事件，则写入数据
                        write(key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 这里处理的主要目的是处理Jdk的一个bug，该bug会导致Selector被意外触发，但是实际上没有任何事件到达，
    // 此时的处理方式是新建一个Selector，然后重新将当前Channel注册到该Selector上
    private Set<SelectionKey> select() throws IOException {
        selector.select();
        Set<SelectionKey> keys = selector.selectedKeys();
        if (keys.isEmpty()) {
            int interestOps = key.interestOps();
            selector = Selector.open();
            key = channel.register(selector, interestOps);
            return select();
        }

        return keys;
    }

    // 读取客户端发送的数据
    private void read(SelectionKey key) throws IOException {

        // 对读取的数据进行业务处理
        executorService.execute(()->processRead());
        input.clear();
        // 读取完成后监听写入事件
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void write(SelectionKey key) throws IOException {
        executorService.execute(()-> processWrite());
    }

    // 进行业务处理，并且获取处理结果。本质上，基于Reactor模型，如果这里成为处理瓶颈，
    // 则直接将其处理过程放入线程池即可，并且使用一个Future获取处理结果，最后写入客户端Channel
    private void processRead() {
        try {
            channel.read(input);
            if (input.position() == 0) {
                return;
            }

            input.flip();
            byte[] bytes = new byte[input.remaining()];
            input.get(bytes);
            String message = new String(bytes, CharsetUtil.UTF_8);
            System.out.println("receive message from client: \n" + message);

            output.put("hello client".getBytes());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processWrite() {
        try {
            output.flip();
            if (channel.isOpen()) {
                // 当有写入事件时，将业务处理的结果写入到客户端Channel中
                channel.write(output);
                output.clear();
                channel.register(selector,SelectionKey.OP_READ);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
