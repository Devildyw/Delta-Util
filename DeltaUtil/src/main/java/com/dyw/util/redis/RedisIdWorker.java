package com.dyw.util.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 全局id生成器
 *
 * @author Devil
 * @create 2022-03-11 21:12
 */
@Component
public class RedisIdWorker {

    /**
     * 开始时间戳
     */
    private static final long BEGIN_TIMESTAMP = 1640995200L;

    /**
     * 左移 移动的比特位
     */
    private static final long COUNT_BITS = 32;
    /**
     * StringRedisTemplate
     */
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 构造器
     *
     * @param stringRedisTemplate StringRedisTemplate工具类
     */
    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 生成器全局id生成器
     *
     * @param keyPrefix key的前缀
     * @return long 返回的id值
     */
    public long nextId(String keyPrefix) {
        //1. 生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;
        //2. 生成序列号
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        Long increment = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);

        //3.拼接并返回

        return timestamp << COUNT_BITS | increment;
    }


}
