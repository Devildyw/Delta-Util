package com.dyw.util.redis.cache;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @author Devil
 * @create 2022-03-11 18:18
 */
@Log4j2
@Component
public class CacheClient {
    /**
     * 创建线程池
     */
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = new ThreadPoolExecutor(10
            , 10
            , 5
            , TimeUnit.SECONDS
            , new LinkedBlockingQueue<>()
            , Executors.defaultThreadFactory()
            , new ThreadPoolExecutor.DiscardOldestPolicy());
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 创建stringRedisTemplate
     *
     * @param stringRedisTemplate stringRedisTemplate
     */
    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 封装RedisData的逻辑过期时间
     *
     * @param key   键
     * @param value 值
     * @param time  逻辑过期时间
     * @param unit  时间单位
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        //设置逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(time));
        //写入Redis中
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 存储数据到redis缓存中
     *
     * @param key   key
     * @param value value
     * @param time  到期时间
     * @param unit  时间精度
     */
    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    /**
     * 缓存穿透解决方案
     *
     * @param keyPrefix  key前缀
     * @param id         业务表中id
     * @param type       传入的返回类型
     * @param dbFallback 传入的函数 这里是数据库的操作函数 可以传入this::getById
     * @param time       过期时间
     * @param unit       时间精度
     * @param <R>        定义的返回类型
     * @param <ID>       定义的id类型
     * @return R
     */
    public <R, ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        //1. 从redis查询业务数据缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2. 判断是否存在
        if (StrUtil.isNotBlank(json)) {
            //3. 存在直接返回
            return JSONUtil.toBean(json, type);
        }
        //判断命中的是否是空值
        if (json != null) {
            //返回一个错误信息
            return null;
        }

        //不存在, 根据id查询数据库
        R r = dbFallback.apply(id);
        //5. 不存在,返回错误
        if (r == null) {
            //将空值写入redis
            stringRedisTemplate.opsForValue().set(key, "", time, unit);
            //返回错误信息
            return null;
        }

        //6. 存在,写入redis
        this.set(key, r, time, unit);
        return r;
    }

    /**
     * 缓存击穿解决方案之 使用逻辑过期时间
     *
     * @param keyPrefix     key前缀
     * @param id            id
     * @param type          返回类型
     * @param dbFallback    传入的函数 这里是数据库的操作函数 可以传入this::getById
     * @param lockKeyPrefix lockKey的前缀
     * @param time          过期时间
     * @param unit          时间精度
     * @param <R>           定义的返回类型的泛型
     * @param <ID>          定义的id类型的泛型
     * @return R
     */
    public <R, ID> R queryWithLogicalExpire(String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, String lockKeyPrefix, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        //1. 从redis查询业务数据缓存
        String json = stringRedisTemplate.opsForValue().get(key);
        //2. 判断是否存在
        if (StrUtil.isBlank(json)) {
            //3. 存在直接返回
            return null;
        }
        //4. 命中,需要先把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        //5. 判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            //5.1 未过期,直接返回业务信息
            return r;
        }
        //5.2 已过期,需要缓存重建
        //6. 缓存重建
        //6.1 获取互斥锁
        String lockKey = lockKeyPrefix + id;
        boolean isLock = tryLock(lockKey);
        //6.2 判断是否获取锁成功
        if (isLock) {
            //6.3 成功,开启独立线程,实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    //查询数据库
                    R r1 = dbFallback.apply(id);
                    //写入redis
                    this.setWithLogicalExpire(key, r1, time, unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    //释放锁
                    unlock(lockKey);
                }
            });
        }

        return r;

    }

    /**
     * 获得互斥锁
     *
     * @param key key
     * @return true:符合，false：不符合
     */
    private boolean tryLock(String key) {
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(aBoolean);
    }

    /**
     * 释放互斥锁
     *
     * @param key key
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
