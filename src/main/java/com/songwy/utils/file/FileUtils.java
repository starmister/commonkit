package com.songwy.utils.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @author: WEIYI SONG
 * @time: 02 14:18
 **/
public class FileUtils {

    public static void fileCopy(String source,String target)throws Exception{
        try(InputStream in = new FileInputStream(source)){
            try(OutputStream out = new FileOutputStream(target)){
                byte[] buffer = new byte[4068];
                int bytesToRead;
                while((bytesToRead = in.read(buffer))!=-1){
                    out.write(buffer,0,bytesToRead);
                }
            }
        }
    }

    public static void main(String[] args) {


        String s1 = "Programming";
        String s2 = new String("Programming");
        String s3 = "Program"; String s4 = "ming";
        String s5 = "Program" + "ming";//+两边是字符串常量是，jvm会进行优化，不使用StringBuilder
        String s6 = s3 + s4;//+两边是引用的时候，两边用new StringBuilder
        System.out.println(s1 == s2);//false
        System.out.println(s1 == s5);//true
        System.out.println(s1 == s6);//false
        System.out.println(s1 == s6.intern());//String intern会把字符串对象放到常量池中，true
        System.out.println(s2 == s2.intern());//false
        String s7 = null+"test";
        System.out.println(s7);//nulltest
        String aa = null;
        Executors.newFixedThreadPool(10);
        ExecutorService service = new ThreadPoolExecutor(10,100,1000, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10));


    }
}
