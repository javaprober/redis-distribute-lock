package com.hyxt.distribute.lock.base;

import com.hyxt.distribute.lock.RedisLock;
import org.redisson.core.RLock;

/**
 * Created by andy on 2016/8/8. */
public class RedissonRequest implements Runnable{

    private String tag;
    public RedissonRequest(String name) {
        this.tag = tag;
    }

    public void run() {
        long begin = System.currentTimeMillis();
        RLock lock = null;
        try {
            lock = RedisLock.lock("", tag);
            LockThreadRequest.i ++;
            long end = System.currentTimeMillis();
            System.out.println("执行" + "-" + tag + "执行时间:" + (end - begin) + "毫秒,操作i值为:" + LockThreadRequest.i );
        } catch (InterruptedException e) {
            System.out.println("获取锁失败");
        } finally {
            RedisLock.unlock(lock);
        }
    }
}
