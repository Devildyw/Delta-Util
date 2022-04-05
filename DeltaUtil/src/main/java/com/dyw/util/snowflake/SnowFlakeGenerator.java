package com.dyw.util.snowflake;

import com.dyw.util.snowflake.work.IDWorker;

/**
 * @author Devil
 * @create 2022-04-05 21:41
 */
public class SnowFlakeGenerator {
    private final IDWorker idWorker;

    /**
     * 有参构造 需要传入workerId 和 datacenterId
     * @param workerId 机器编号
     * @param datacenterId 数据中心编号
     */
    public SnowFlakeGenerator(long workerId, long datacenterId) {
        this.idWorker = new IDWorker(workerId, datacenterId);
    }

    /**
     * 简便雪花算法ID生成器 无参默认两个参数位0L
     */
    public SnowFlakeGenerator(){
        this.idWorker = new IDWorker();
    }

    /**
     * 生成下一个id生成策略由构造方法的两个参数决定
     * @return snowflakeId
     */
    public long NextId(){
        return this.idWorker.nextId();
    }
}
