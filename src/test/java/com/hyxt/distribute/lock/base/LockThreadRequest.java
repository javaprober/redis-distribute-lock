package com.hyxt.distribute.lock.base;

import org.redisson.RedissonClient;
import org.redisson.core.RLock;

import java.util.concurrent.TimeUnit;

/**
 * Created by andy on 2016/8/8. */
public class LockThreadRequest extends Thread{

    String name;

    RedissonClient redissonClient;

    static int i = 0 ;

    public LockThreadRequest(String name, RedissonClient redissonClient) {
        super();
        this.name = name;
        this.redissonClient = redissonClient;
    }

    public void run() {

        long begin = System.currentTimeMillis();
        RLock lock = redissonClient.getLock(name);
        try {
            lock.tryLock(100, 1, TimeUnit.SECONDS);//第一个参数代表等待时间，第二是代表超过时间释放锁，第三个代表设置的时间制
            i ++;
            long end = System.currentTimeMillis();
            System.out.println("执行" + "-" + name + "执行时间:" + (end - begin) + "毫秒,操作i值为:" + i );
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
