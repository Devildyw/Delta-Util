package com.dyw.util.redis.cache;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Devil
 * @create 2022-03-11 18:22
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;

    private Object data;
}
