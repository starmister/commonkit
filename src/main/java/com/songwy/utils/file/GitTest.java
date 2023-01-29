package com.songwy.utils.file;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GitTest {

    //static Logger logger = LoggerFactory.getLogger("GitTest");
    public static void main(String[] args)throws Exception {
//        System.out.println("本地生成git账号进行测试。。。");
//        System.out.println(System.currentTimeMillis());
//        System.out.println(new Date());
//        System.out.println("本地生成git账号进行测试。。。");
//        System.out.println(new Date()+"dddddddd");
//        String companyID = "null";
//        String git = "gitTest";
//
//        Logger logger = LoggerFactory.getLogger(
//                GitTest.class);
//        logger.error("Hello World");
//        logger.info("日志框架测试：{},{}",companyID,git);
//        System.out.println(logger.getClass());

//        List<String> list =  new
//                ArrayList<>();
//        list.add("s");
//        list.add("2");
//        System.out.println(list);
        //String data = "2020-11-02";
        //System.out.println(GetTimestamp(data));
        //System.out.println(formatContractYmd(new Date()));
//        lable1:
//        while(true){
//
//            if(i == 6){
//                System.out.println("lable1结束");
//                break;
//            }
//
//            lable2:
//            while(true){
//                if(i == 5){
//                    System.out.println("将从lable2将跳到lable1");
//                    i++;
//                    continue lable1;
//                }
//                i++;
//            }
//        }


        Long timeStamp = Long.valueOf(1480563285);  //获取当前时间戳
//        System.out.println(timeStamp);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sd = sdf.format(timeStamp);
////        String sd2 = sdf.format(new Date(timeStamp));
////        String sd3 = sdf.format(timeStamp);
       System.out.println("格式化结果：" + sd);
        System.out.println(unixToDate(timeStamp));
////        System.out.println("格式化结果：" + sd2);
////        System.out.println("格式化结果：" + sd3);
//        String time = "2021-11-15";
//        Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(time);
//        Long time1 = parse.getTime();
//        System.out.println(time1);


    }

    static int i =0;

    public static Date unixToDate(Long time) {
        return new Date(time * 1000L);
    }
    public static String GetTimestamp(String date) throws Exception {
        Calendar cal = Calendar.getInstance();
        if (date.contains("-")) {
            date = date.replaceAll("-", "/");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            cal.setTime(dateFormat.parse(date));
            return String.valueOf(cal.getTimeInMillis() / 1000L);
        } else {
            return date;
        }
    }

    public static String formatContractYmd(Date dt) {
        return contractymd.get().format(dt);
    }

    private static ThreadLocal<DateFormat> contractymd = new ThreadLocal<DateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy年M月d日");
        }
    };


}
