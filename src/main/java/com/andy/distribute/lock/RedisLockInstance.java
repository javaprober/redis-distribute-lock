package com.andy.distribute.lock;

import com.andy.distribute.lock.exception.RedisLockException;
import com.andy.distribute.lock.client.RedisClientHandler;
import org.redisson.RedissonClient;

/**
 * Created by andy on 2016/8/11.
 */
public class RedisLockInstance {
//    private static Logger logger= LoggerFactory.getLogger(RedisLockInstance.class);

    public static RedissonClient getClient () throws RedisLockException {
        RedissonClient client = null;
        try {
            client = RedisClientHandler.Factory.getInstance().asMode();
        } catch (Exception e) {
//            logger.error("redis lock connection fail !!!");
            throw new RedisLockException("Redis lock create connection failed :"+e.getMessage());
        }
        return client;
    }
}
