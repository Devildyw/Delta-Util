package com.dyw.util.snowflake.work;

import org.springframework.beans.factory.annotation.Value;

import java.net.Inet4Address;
import java.net.InetAddress;

/**
 * @author Devil
 * @create 2022-04-05 21:01
 */
@SuppressWarnings("all")
public class IDWorker {
    //十位的工作机器码
    private long workerId = 0L; //工作id 五位
    private long datacenterId = 0L; //数据中心id 五位

    //12位序列号
    private long sequence = 0L;

    //初始时间戳
    private final long twEpoch = 1649163644246L;

    //长度为5位
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    //最大值
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);

    //序列号id长度
    private final long sequenceBits = 12L;
    private final long sequenceMask = ~(-1L << sequenceBits);

    //工作id需要左移的位数, 12位(序列号的位长)
    private final long workerIdShift = sequenceBits;
    //数据中心id需要左移的位数 序列号长+工作id长
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    //时间戳左移位数 = 序列号长+工作id长+工作位长
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    //上次时间戳, 初始值位负值
    private long lastTimestamp = -1L;

    //无参构造
    public IDWorker(){
    }
    /**
     * 构造方法
     * @param workerId 工作节点id
     * @param datacenterId 数据中心id
     */
    public IDWorker(long workerId, long datacenterId) {
        //检查参数的合法性
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        System.out.printf("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, workerid %d",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public long getSequence() {
        return sequence;
    }

    /**
     * 下一个ID生成算法
     * @return snowflakeId
     */
    public synchronized long nextId() {
        //先获取当前系统时间
        long timestamp = timeGen();
        //如果当前系统时间比上次获取id时间戳小就抛出异常 时钟往后移动可能会出现同样id所以这里必须抛异常结束执行
        if (timestamp < lastTimestamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.",lastTimestamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        //获取当前时间戳如果等于上次时间戳(同一毫秒内),则在序列号加一,否则序列号赋值为0, 从零开始
        if(timestamp==lastTimestamp){
            //这是使用&sequenceMask是为了防止sequence溢出12位(前面要求了sequence的长度只能是12位)
            sequence = (sequence+1)&sequenceMask;
            //如果防止刚好移除经过&sequenceMask后 会变成0 可能会发生重复的情况
            //所以此时需要再次获取时间戳,并于上次时间戳作比较 直到与上次时间戳不一致返回当前时间戳避免重复
            if(sequence==0){
                timestamp = tilNextMillis(lastTimestamp);
            }
        }//如果不在同一个时间戳中 代表该序列刚开始计数所以初始为0
        else{
            sequence = 0;
        }

        //将上次时间戳值更新
        lastTimestamp = timestamp;


        /*
         * 返回结果：
         * (timestamp - TwEpoch) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数
         * (datacenterId << datacenterIdShift) 表示将数据id左移相应位数
         * (workerId << workerIdShift) 表示将工作id左移相应位数
         * | 是按位或运算符，例如：x | y，只有当x，y都为0的时候结果才为0，其它情况结果都为1。
         * 因为个部分只有相应位上的值有意义，其它位上都是0，所以将各部分的值进行 | 运算就能得到最终拼接好的id
         */
        return ((timestamp - twEpoch)<<timestampLeftShift) |
                (datacenterId<<datacenterIdShift) |
                (workerId<<workerIdShift)|
                sequence;



    }

    /**
     * 获取时间戳,并于上次时间戳作比较
     * @param lastTimestamp 上一次获取的时间戳
     * @return timestamp 更新后的系统时间
     */
    private long tilNextMillis(long lastTimestamp){
        long timestamp = timeGen();
        while(timestamp<=lastTimestamp){
            timestamp = timeGen();
        }
        return timestamp;

    }

    /**
     * 获取系统时间戳
     * @return 系统时间戳
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }


}
