package com.songwy.utils.io.nio.reactor.multi_reactor;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
public class SubReactor implements Runnable{

    final Selector mySelector;
    SocketChannel sc;



    public SubReactor() throws Exception {

        // 每个SubReactor 一个selector
        this.mySelector = SelectorProvider.provider().openSelector();
    }

    public void setSc(SocketChannel sc) {
        this.sc = sc;
    }

    @Override
    public void run() {
        try{
            new MultiHandler(sc,mySelector).run();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }


}
