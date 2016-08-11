package com.hyxt.distribute.lock.base;

import com.hyxt.distribute.lock.client.RedisClientHandler;
import junit.framework.TestCase;
import org.junit.Test;
import org.redisson.Config;
import org.redisson.RedissonClient;
import org.redisson.SingleServerConfig;

/**
 * Created by andy on 2016/8/9.
 */
public class RedissionBaseTest extends TestCase{

    protected RedissonClient redissonClient;

    public void setUp() {
        System.out.println("1s");
        Config config = new Config();
        SingleServerConfig singleSerververConfig = config.useSingleServer();
        singleSerververConfig.setAddress("127.0.0.1:6379");
        //redisson客户端
        redissonClient = RedisClientHandler.Factory.redissonClient;
    }

    @Test
    public void testNN() {

    }

    public void tearDown() {
//        RedisUtils.getInstance().closeRedisson(redissonClient);
    }
}
