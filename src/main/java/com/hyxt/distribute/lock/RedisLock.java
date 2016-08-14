package com.hyxt.distribute.lock;

import com.hyxt.distribute.lock.util.PropertyUtils;
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
        String waitTimeStr = PropertyUtils.getPropertyString("base_config", "redis.lock.default.waittime");
        String releaseTimeStr = PropertyUtils.getPropertyString("base_config", "redis.lock.default.timeout");

        if(waitTimeStr == null || "".equals(waitTimeStr)) {
            //默认等待1秒
            waitTimeStr = "1000";
        }
        if(releaseTimeStr == null || "".equals(releaseTimeStr)) {
            //默认1秒超时释放
            releaseTimeStr = "1000";
        }

        return lock(appNo,bizNo, Integer.parseInt(waitTimeStr),Integer.parseInt(releaseTimeStr));
    }

    /**
     * 加锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @param waitTime 获取锁等待时间/毫秒
     * @param releaseTime  超过releaseTime时间释放锁/毫秒
     * @return RLock
     */
    public static RLock lock(String appNo,String bizNo ,int waitTime ,int releaseTime) throws InterruptedException {


        RedissonClient client = RedisLockInstance.getClient();
        RedisUtils redisUtils = RedisUtils.getInstance();
        RLock rLock = null;
        try {
            String reqTag = appNo + "_" + bizNo;
            rLock = redisUtils.getRLock(client,reqTag);
            boolean hasLock = rLock.tryLock(waitTime, releaseTime, TimeUnit.MILLISECONDS);
//            boolean hasLock = rLock.tryLock();
            if(!hasLock) {
                logger.error("获取锁失败");
                throw new InterruptedException("获取锁失败");
            }
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
        if(lock != null) {
            lock.unlock();
        }
        lock = null;
    }
}
