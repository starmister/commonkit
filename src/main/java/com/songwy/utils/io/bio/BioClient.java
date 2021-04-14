package com.songwy.utils.io.bio;



import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by lanux on 2017/8/6.
 */
public class BioClient extends BioBasic implements Closeable {

    public Socket socket;

    public BioClient init() throws Exception {
        socket = new Socket(NetConfig.SERVER_IP, NetConfig.SERVER_PORT);
        socket.setKeepAlive(true);
        socket.setSoTimeout(NetConfig.SO_TIMEOUT);
        return this;
    }

    @Override
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public void write(String value) throws IOException {
        byte[] bytes = value.getBytes();
        System.out.println("client write " + bytes.length + " : " + value);
        super.writeStream(socket.getOutputStream(), bytes);
    }

    public String read() throws IOException {
        byte[] bytes = super.readStream(socket.getInputStream());
        String s = new String(bytes);
        System.out.println("client read " + bytes.length + " : " + s);
        return s;
    }

    public static void main(String[] args) throws Exception{
        BioClient bioClient = new BioClient();
        bioClient.init();
        bioClient.write("hello,world");
    }
}
