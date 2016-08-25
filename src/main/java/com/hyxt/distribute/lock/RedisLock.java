package com.hyxt.distribute.lock;

import com.hyxt.distribute.lock.client.PropertiesHandle;
import com.hyxt.distribute.lock.exception.RedisLockException;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁
 * Created by andy on 2016/8/10.
 */
public class RedisLock {

    /*private static Logger logger= LoggerFactory.getLogger(RedisLockGrain.class);*/

    private final static ConcurrentMap<String,RLock> lockMap = new ConcurrentHashMap<String,RLock>();

    private final static ConcurrentMap<String,Semaphore> semaphores = new ConcurrentHashMap<String,Semaphore>();

    /**
     * 获取锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @return RLock
     */
    public static RLock lock(String appNo,String bizNo) throws RedisLockException {
        return lock(appNo,bizNo, null,null);
    }

    /**
     * 获取锁
     * @param serviceIndication 唯一锁标示名称路径
     * @return RLock
     */
    public static RLock lock(String serviceIndication) throws RedisLockException {
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
    public static RLock lock(String appNo,String bizNo
            ,Integer waitTime ,Integer releaseTime) throws RedisLockException {
        String serviceIndication = getLockName(appNo,bizNo);
        return lock(serviceIndication,waitTime,releaseTime);
    }

    /**
     * 获取锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @param waitTime 获取锁等待时间/毫秒
     * @return RLock
     */
    public static RLock lockByWaitTime(String appNo,String bizNo ,Integer waitTime) throws RedisLockException {

        String releaseTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.timeout");

        String waitTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.waittime");
        if(waitTime == null || waitTime <= 0) {
            //默认等待1秒
            waitTime = Integer.parseInt(waitTimeStr);
        }

        return lock(appNo,bizNo,waitTime,Integer.parseInt(releaseTimeStr));
    }


    /**
     * 获取锁
     * @param appNo application 唯一编号
     * @param bizNo 业务场景唯一编号
     * @param releaseTime 超过releaseTime时间释放锁/毫秒
     * @return RLock
     */
    public static RLock lockByTimeout(String appNo,String bizNo ,Integer releaseTime) throws RedisLockException {

        String releaseTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.timeout");

        String waitTimeStr = PropertiesHandle.Factory.propertiesValue("redis.lock.default.waittime");
        if(releaseTime == null || releaseTime <= 0) {
            //默认等待1秒
            releaseTime = Integer.parseInt(releaseTimeStr);
        }

        return lock(appNo,bizNo,Integer.parseInt(waitTimeStr),releaseTime);
    }

    public static RLock lock(String serviceIndication,Integer waitTime,Integer releaseTime) throws RedisLockException {
        RLock rLock = null;
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
        Semaphore semaphore = null;
        try {
            rLock = lockMap.get(serviceIndication);
            semaphore = semaphores.get(serviceIndication);
            if(rLock == null) {
                rLock = client.getLock(serviceIndication);
                lockMap.put(serviceIndication,rLock);
            }
            if(semaphore == null) {
                semaphore = new Semaphore(1);
                semaphores.put(serviceIndication,semaphore);
            }
            boolean accquireSemaphore = semaphore.tryAcquire(releaseTime*2, TimeUnit.MILLISECONDS);
            if(accquireSemaphore) {
//              logger.info("getLockName is :{}" , serviceIndication);
                rLock = client.getLock(serviceIndication);

                boolean hasLock = rLock.tryLock(waitTime, releaseTime, TimeUnit.MILLISECONDS);
                if (!hasLock) {
                    if(semaphore != null) {
                        semaphore.release();
                    }
                    //                logger.error("Failed to obtain a lock");
                    throw new RedisLockException("Failed to obtain a lock");
                }
            } else {
                throw new RedisLockException("Obtain a lock timeout");
            }

        } catch (InterruptedException e) {
//            logger.error("Failed to obtain a lock ,exception:{}" , e);
            if(semaphore != null) {
                semaphore.release();
            }
            throw new RedisLockException(e);
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
                Semaphore semaphore = semaphores.get(lock.getName());
                if(semaphore != null) {
                    semaphore.release();
                }
            }
            lock = null;
        } catch(Exception e) {
//            logger.error("Failed to unlock,exception:{}" , e);
        }
    }


    private static String getLockName(String appNo,String bizNo) {
        if(appNo == null || "".equals(appNo)) {
            appNo = "redisson";
        }

        if(bizNo == null || "".equals(bizNo)) {
            bizNo = "applicationLock";
        }
        return appNo + "_" + bizNo;
    }
}
