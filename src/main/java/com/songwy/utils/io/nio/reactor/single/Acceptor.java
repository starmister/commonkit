package com.songwy.utils.io.nio.reactor.single;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * 连接事件就绪,处理连接事件
 */
class Acceptor implements Runnable {

    private final ServerSocketChannel serverSocket;
    private final Selector selector;

    public Acceptor(ServerSocketChannel serverSocket,Selector selector) {
        this.serverSocket = serverSocket;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            SocketChannel c = serverSocket.accept();
            // 注册读写
            if (c != null) {
                new Handler(c,selector).run();
            }
        } catch (Exception e) {

        }
    }
}
