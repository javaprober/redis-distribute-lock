package com.hyxt.distribute.lock.base;

import com.hyxt.distribute.lock.RedisLockInstance;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.redisson.RedissonClient;

import java.io.IOException;

/**
 * Created by andy on 2016/8/9.
 */
public abstract class RedissionBaseTest{

    protected RedissonClient redisson;
    protected static RedissonClient defaultRedisson;

    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException {
        defaultRedisson = createInstance();
    }

    @AfterClass
    public static void afterClass() throws IOException, InterruptedException {
        defaultRedisson.shutdown();
    }

    @Before
    public void before() throws IOException, InterruptedException {
        if (redisson == null) {
            redisson = defaultRedisson;
        }
    }

    @After
    public void after() throws InterruptedException {
        redisson.shutdown();
    }

    public static RedissonClient createInstance() {
        return RedisLockInstance.getClient();
    }
}
