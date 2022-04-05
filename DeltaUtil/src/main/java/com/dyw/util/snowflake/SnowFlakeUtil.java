package com.dyw.util.snowflake;

import com.dyw.util.snowflake.work.IDWorker;
import org.springframework.stereotype.Component;

/**
 * @author Devil
 * @create 2022-04-05 21:07
 */
public class SnowFlakeUtil {
    private static final IDWorker idWorker = new IDWorker();


    /**
     * 获取雪花算法生成的ID
     *
     * @return SnowFlakeId
     */
    public static long createSnowFlakeId() {
        return idWorker.nextId();
    }

    /**
     * 获取雪花算法中的当前的序列号
     *
     * @return Sequence
     */
    public static long getSequence() {
        return idWorker.getSequence();
    }
}
