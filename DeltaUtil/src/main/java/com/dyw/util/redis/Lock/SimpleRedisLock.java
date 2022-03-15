package com.dyw.util.redis.Lock;

import cn.hutool.core.lang.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 实现分布式锁
 *
 * @author Devil
 * @create 2022-03-12 15:00
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimpleRedisLock implements ILock {
    /**
     * 锁的前缀
     */
    private static final String KEY_PREFIX = "lock:";
    /**
     * UUID
     */
    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";
    private static DefaultRedisScript<Long> UNLOCK_SCRIPT = null;

    static {
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    private StringRedisTemplate stringRedisTemplate;
    private String name;

    @Override
    public boolean tryLock(long timeoutSec) {
        //获取线程表示
        String threadId = ID_PREFIX + Thread.currentThread().getId();
        //获取锁
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(KEY_PREFIX + name, threadId, timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void unlock() {
        //调用lua脚本
        stringRedisTemplate.execute(UNLOCK_SCRIPT,
                Collections.singletonList(KEY_PREFIX + name),
                ID_PREFIX + Thread.currentThread().getId());

    }
}
