package com.dyw.util.redis.Lock;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 实现分布式锁
 * @author Devil
 * @create 2022-03-12 15:00
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleRedisLock implements ILock{
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 锁的前缀
     */
    private static final String KEY_PREFIX = "lock:";
    private String name;

    /**
     * UUID
     */
    private static final String ID_PREFIX = UUID.randomUUID().toString(true)+"-";

    @Override
    public boolean tryLock(long timeoutSec) {
        //获取线程表示
        String threadId = ID_PREFIX+Thread.currentThread().getId();
        //获取锁
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + name, threadId, timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unlock() {
        //获取线程标识
        String threadId = ID_PREFIX+Thread.currentThread().getId();
        //获取锁中的标识
        String id = stringRedisTemplate.opsForValue().get(KEY_PREFIX + name);
        //判断是否一致
        if(threadId.equals(id)){
            //一致就释放锁
            stringRedisTemplate.delete(KEY_PREFIX+name);
        }
        //否则就不释放
    }
}
