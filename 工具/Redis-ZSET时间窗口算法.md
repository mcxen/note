## Redis-ZSET时间窗口算法

又名滑动时间算法，所谓的滑动时间算法指的是以当前时间为截止时间，往前取一定的时间，比如取1s的时间，在这1s时间内最大的访问数为1000。把这1秒分为1000格，每格是1毫秒。

滑动时间窗口如下图所示：

![img](https://fastly.jsdelivr.net/gh/52chen/imagebed2023@main/uPic/1dc7c36f244c4c9386a204c01fea29d4~tplv-k3u1fbpfcp-jj-mark:3024:0:0:0:q75.png)

 

其中每一个小格子代表1ms，比如1s允许200次请求，那么就分成1000个小格。

## 如何实现？

借助Redis的有序集合ZSet来实现时间窗口算法限流，实现的过程是：

第一步：先使用ZSet的key存储限流的ID，score用来存储请求的时间。

第二步：每次有请求访问来了之后，先判断当前时间（以ms为单位如：System.currentTimeMillis()）往前推1000ms的时间，获取这两个值的数量，如果超过限流值，就实行限流。

代码如下：

 

```java
package com.bxm.warcar.cache.test.fetcher;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.List;


/**
 * <h3>
 * 基于redis的时间滑动窗口
 * </h3>
 */
public class Test {

    private final static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "10.10.1.34", 6379,
            Protocol.DEFAULT_TIMEOUT, "redis_pwd123", 0);
    private static List<String> list = new ArrayList<>();

    /**
     * 设定 有1000个请求进来，但是每秒只能接受200个请求
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        int a = 1000;
        int limit =200;
        final long l = System.currentTimeMillis();

        for (int i = 0; i < a; i++) {

            add("LIMIT:TEST", "TEST" + i, limit);
            Thread.sleep(1);
        }
        System.out.println(System.currentTimeMillis()-l);
        System.out.println(list);
    }

    /**
     *
     * @param key 固定的
     * @param value 变化的，同一个value只能存入一次
     * @param limit  限流参数，可调整
     */
    private static void add(String key, String value, int limit) {

        final long timeMillis = System.currentTimeMillis();
        if (get(key, timeMillis) > limit) {
            System.out.println(value+"超过限制了");
            list.add(value);
            return;
        }

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.zadd(key, timeMillis, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }

    /**
     * 获取当前 timeMillis 往前退1秒的数据
     * @param key  固定的
     * @param timeMillis 动态的，以毫秒为单位统计
     * @return
     */
    private static long get(String key, long timeMillis) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zcount(key, timeMillis - 1000, timeMillis);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
```