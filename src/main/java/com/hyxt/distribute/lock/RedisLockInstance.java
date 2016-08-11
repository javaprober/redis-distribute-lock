package com.hyxt.distribute.lock;

import com.hyxt.distribute.lock.client.RedisClientHandler;
import org.redisson.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andy on 2016/8/11.
 */
public class RedisLockInstance {
    private static Logger logger= LoggerFactory.getLogger(RedisLockInstance.class);

    public static RedissonClient getClient () {
        RedissonClient client = null;
        try {
            client = RedisClientHandler.Factory.getInstance().asMode();
        } catch (Exception e) {
            logger.error("redis lock connection fail !!!");
        }
        return client;
    }
}
