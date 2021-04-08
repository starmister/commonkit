package com.songwy.utils.io.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NioClient {
    public static void main(String[] args) throws Exception{

        SocketChannel.open(new InetSocketAddress("127.0.0.1",8000));
    }
}
