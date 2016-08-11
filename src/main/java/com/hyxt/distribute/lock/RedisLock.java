package com.hyxt.distribute.lock;

import com.hyxt.distribute.lock.client.RedisClientHandler;
import com.hyxt.distribute.lock.util.RedisUtils;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁
 * Created by andy on 2016/8/10.
 */
public class RedisLock {

    private static Logger logger= LoggerFactory.getLogger(RedisLock.class);

    /**
     * 加锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @return RLock
     */
    public static RLock lock(String appNo,String bizNo) throws InterruptedException {
        return lock(appNo,bizNo,1000,1000);
    }

    /**
     * 加锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @param waitTime 获取锁等待时间/毫秒
     * @param timeout  超过timeout时间释放锁/毫秒
     * @return RLock
     */
    public static RLock lock(String appNo,String bizNo ,int waitTime ,int timeout) throws InterruptedException {
        RedissonClient client = RedisClientHandler.Factory.redissonClient;
        RedisUtils redisUtils = RedisUtils.getInstance();
        RLock rLock = redisUtils.getRLock(client, appNo + "_" + bizNo);
        try {
            rLock.tryLock(waitTime,timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("获取锁异常:" + e);
            throw e;
        }
        return rLock;
    }

    /**
     * 解锁
     * @param lock 已经获取到的锁
     *
     */
    public static void unlock(RLock lock) {
        lock.unlock();
    }
}
