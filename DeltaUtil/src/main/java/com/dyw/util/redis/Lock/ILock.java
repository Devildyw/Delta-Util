package com.dyw.util.redis.Lock;

/**
 * Redis实现分布式锁
 *
 * @author Devil
 * @create 2022-03-12 14:57
 */
public interface ILock {
    /**
     * 获取锁
     *
     * @param timeoutSec 过期时间
     * @return true表示成功 false表示失败
     */
    boolean tryLock(long timeoutSec);

    /**
     * 释放锁
     */
    void unlock();
}
