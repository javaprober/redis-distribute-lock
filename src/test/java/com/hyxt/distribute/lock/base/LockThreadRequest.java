package com.hyxt.distribute.lock.base;

import com.hyxt.distribute.lock.util.RedisUtils;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;

/**
 * Created by andy on 2016/8/8. */
public class LockThreadRequest extends Thread{

    String tag;

    RedissonClient redissonClient;

    public static int i = 0 ;

    public LockThreadRequest(String name, RedissonClient redissonClient) {
        this.tag = name;
        this.redissonClient = redissonClient;
    }

    public void run() {

        long begin = System.currentTimeMillis();
        RedisUtils.getInstance().getRLock(redissonClient, tag);
        RLock lock = redissonClient.getLock(tag);
        try {
            //第一个参数代表等待时间，第二是代表超过时间释放锁，第三个代表设置的时间制
//            if(lock.tryLock(100, 0, TimeUnit.SECONDS)) {
            if(lock.tryLock()) {
                i ++;
                long end = System.currentTimeMillis();
                System.out.println("执行" + "-" + tag + "执行时间:" + (end - begin) + "毫秒,操作i值为:" + i );
            }
        } catch (Exception e) {
            System.out.println("获取锁失败");
        } finally {
            lock.unlock();
        }
    }
}
