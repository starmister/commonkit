package com.songwy.utils.io.nio.reactor.multi_reactor;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiAcceptor implements Runnable{
    private final ServerSocketChannel serverSocket;
    // cpu线程数相同多work线程
    int workCount =Runtime.getRuntime().availableProcessors();
    SubReactor[] workThreadHandlers = new SubReactor[workCount];

    volatile int nextHandler = 0;

    public MultiAcceptor(ServerSocketChannel serverSocket) {
        this.serverSocket = serverSocket;
        nextHandler = 0;
        for (int i = 0; i < workThreadHandlers.length; i++) {
            try {
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                workThreadHandlers[i] = new SubReactor(executorService);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void run() {
        try {
            SocketChannel c = serverSocket.accept();
            if (c != null) {// 注册读写
                synchronized (c) {
                    // 顺序获取SubReactor，然后注册channel
                    SubReactor work = workThreadHandlers[nextHandler];
                    work.setSc(c);
                    nextHandler++;
                    if (nextHandler >= workThreadHandlers.length) {
                        nextHandler = 0;
                    }
                    work.executor();
                }
            }
        } catch (Exception e) {
        }
    }
}
