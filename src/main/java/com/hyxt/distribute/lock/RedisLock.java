package com.hyxt.distribute.lock;

import com.hyxt.distribute.lock.client.PropertiesHandle;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁
 * Created by andy on 2016/8/10.
 */
public class RedisLock {

    private static Logger logger= LoggerFactory.getLogger(RedisLock.class);

    private final static ConcurrentMap<String,RLock> lockMap = new ConcurrentHashMap<String,RLock>();

    /**
     * 获取锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @return RLock
     */
    public synchronized static RLock lock(String appNo,String bizNo) throws InterruptedException {
        return lock(appNo,bizNo, null,null);
    }

    /**
     * 获取锁
     * @param serviceIndication 唯一锁标示名称路径
     * @return RLock
     */
    public synchronized static RLock lock(String serviceIndication) throws InterruptedException {
        return lock(serviceIndication, null,null);
    }

    /**
     * 获取锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @param waitTime 获取锁等待时间/毫秒
     * @param releaseTime  超过releaseTime时间释放锁/毫秒
     * @return RLock
     */
    public synchronized static RLock lock(String appNo,String bizNo
            ,Integer waitTime ,Integer releaseTime) throws InterruptedException {
        String serviceIndication = getLockName(appNo,bizNo);
        return lock(serviceIndication,waitTime,releaseTime);
    }

    /**
     * 加锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @param waitTime 获取锁等待时间/毫秒
     * @return RLock
     */
    public synchronized static RLock lockByWaitTime(String appNo,String bizNo ,Integer waitTime) throws InterruptedException {

        String releaseTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.timeout");

        String waitTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.waittime");
        if(waitTime == null || waitTime <= 0) {
            //默认等待1秒
            waitTime = Integer.parseInt(waitTimeStr);
        }

        return lock(appNo,bizNo,waitTime,Integer.parseInt(releaseTimeStr));
    }


    /**
     * 加锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @param releaseTime 超过releaseTime时间释放锁/毫秒
     * @return RLock
     */
    public synchronized static RLock lockByTimeout(String appNo,String bizNo ,Integer releaseTime) throws InterruptedException {

        String releaseTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.timeout");

        String waitTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.waittime");
        if(releaseTime == null || releaseTime <= 0) {
            //默认等待1秒
            releaseTime = Integer.parseInt(releaseTimeStr);
        }

        return lock(appNo,bizNo,Integer.parseInt(waitTimeStr),releaseTime);
    }

    public synchronized static RLock lock(String serviceIndication,Integer waitTime,Integer releaseTime) throws InterruptedException {
        String waitTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.waittime");
        String releaseTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.timeout");
        if(waitTime == null || waitTime <= 0) {
            //默认等待1秒
            waitTime = Integer.parseInt(waitTimeStr);
        }
        if(releaseTime == null || releaseTime <= 0) {
            //默认1秒超时释放
            releaseTime = Integer.parseInt(releaseTimeStr);
        }
        RedissonClient client = RedisLockInstance.getClient();
        RLock rLock = null;
        try {
            rLock = lockMap.get(serviceIndication);
            if(rLock == null) {
                rLock = client.getLock(serviceIndication);
                lockMap.put(serviceIndication,rLock);
            }
//            logger.info("getLockName is :{}" , serviceIndication);
            rLock = client.getLock(serviceIndication);

            boolean hasLock = rLock.tryLock(waitTime, releaseTime, TimeUnit.MILLISECONDS);
            if (!hasLock) {
//                logger.error("Failed to obtain a lock");
                throw new InterruptedException("Failed to obtain a lock");
            }

        } catch (InterruptedException e) {
//            logger.error("Failed to obtain a lock ,exception:{}" , e);
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
        try{
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            lock = null;
        } catch(Exception e) {
            logger.error("Failed to unlock,exception:{}" , e);
        }
    }


    private synchronized static String getLockName(String appNo,String bizNo) {
        if(appNo == null || "".equals(appNo)) {
            appNo = "redisson";
        }

        if(bizNo == null || "".equals(bizNo)) {
            bizNo = "applicationLock";
        }
        return appNo + "_" + bizNo;
    }
}
