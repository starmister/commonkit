package com.songwy.utils.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * 1. 使用redis zset 数据结构
 * 2.使用score排序   score为过期时间点
 * 3.启动线程不断取出排序第一个  比较score和当前时间点   如果score小于或等于当前时间  说明此数据过期  需要处理
 * 4.处理完毕在zset中移除
 */
public class DelayQueue {

    private static final String ADDR="127.0.0.1";
    private static final int PORT=6379;
    //初始化jedis
    private static JedisPool jedisPool=new JedisPool(new JedisPoolConfig(),ADDR,PORT,10000);
    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    //消息入队
    public void productionDelayMessage(){
        //延迟5秒
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.SECOND, 5);
        int second3later = (int) (cal1.getTimeInMillis() / 1000);
        Long orderId = getJedis().zadd("OrderId", second3later, "OID0000001" );
        System.out.println(new Date()+"ms:redis生成了一个订单任务：订单ID为"+"OID0000001"+"==============="+orderId);
    }
    //消费者取订单
    public void consumerDelayMessage(){
        Jedis jedis = getJedis();
        while(true){
            Set<Tuple> items = jedis.zrangeWithScores("OrderId", 0, 1);
            if(items == null || items.isEmpty()){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                continue;
            }
            int  score = (int) ((Tuple)items.toArray()[0]).getScore();
            Calendar cal = Calendar.getInstance();
            int nowSecond = (int) (cal.getTimeInMillis() / 1000);
            if(nowSecond >= score){
                String orderId = ((Tuple)items.toArray()[0]).getElement();
                Long num = jedis.zrem("OrderId", orderId);
                System.out.println(num);
                if( num != null && num>0){
                    System.out.println(new Date() +"ms:redis消费了一个任务：消费的订单OrderId为"+orderId);
                }

            }
        }
    }

    public static void main(String[] args) {
        DelayQueue delayQueue =new DelayQueue();
        delayQueue.productionDelayMessage();
        delayQueue.consumerDelayMessage();
    }
}
