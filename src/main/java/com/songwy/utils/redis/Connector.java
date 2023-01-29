package com.songwy.utils.redis;

import com.songwy.utils.redis.bean.Person;

import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Connector {
    public static void main(String[] args)throws Exception {

        Jedis jedis = new Jedis("127.0.0.1", 6379);
        /**
         * hash
         */
        Map<String,String> map = new HashMap<String, String>();
        map.put("name","songwy");
        map.put("password","123456");
        map.put("sex","fmale");
        jedis.hmset("java",map);

        Person person = new Person();
        person.setName("naonao");
        person.setPassword("111111");
        person.setSex("1");
        jedis.hmset("person",objectToMap(person));
        System.out.println(jedis.hget("person", "password"));


        /**
         * set
         */
        jedis.sadd("personSet","name","password");
        System.out.println(jedis.smembers("personSet"));
        System.out.println(jedis.spop("personSet"));

        /**
         * zset
         */
        jedis.zadd("books",9.0,"think in java");
        jedis.zadd("books",8.9,"spring5核心原理");
        jedis.zadd("books",8.6,"cookbook");
        System.out.println(jedis.zrange("books",0,-1));

        /**
         * 位图
         */
        jedis.setbit("hello",1,true);
        jedis.setbit("hello",2,true);
        jedis.setbit("hello",4,true);
        System.out.println(jedis.get("hello"));
        System.out.println(jedis.getbit("hello",3));

        /**
         * HyperLogLog
         */



    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, String> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, String> map = new HashMap<String,String>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String value = field.get(obj).toString();
            map.put(fieldName, value);
        }
        return map;
    }
}
