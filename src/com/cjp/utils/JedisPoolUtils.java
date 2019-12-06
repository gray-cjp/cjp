package com.cjp.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JedisPoolUtils {
    private static JedisPool pool = null;
    static {
        InputStream is =JedisPool.class.getClassLoader().getResourceAsStream("jedis.properties");
        Properties pro = new Properties();
        try {
            pro.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JedisPoolConfig poolConfig =new JedisPoolConfig();
        poolConfig.setMaxIdle(Integer.parseInt(pro.getProperty("redis.maxIdle")));
        poolConfig.setMinIdle(Integer.parseInt(pro.getProperty("redis.minIdle")));
        poolConfig.setMaxTotal(Integer.parseInt(pro.getProperty("redis.maxTotal")));
        pool = new JedisPool(poolConfig,pro.getProperty("redis.url"),Integer.parseInt(pro.getProperty("redis.port")));

    }
    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void main(String[] args) {
        Jedis jedis =JedisPoolUtils.getJedis();
        System.out.println(jedis.get("username"));
    }
}
