package com.hyxt.distribute.lock.base;

import com.hyxt.distribute.lock.RedisLock;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;

/**
 * Created by andy on 2016/8/8. */
public class LockThreadBadRequest extends LockThreadRequest{

    public LockThreadBadRequest(String name, RedissonClient redissonClient) {
        super(name,redissonClient);
    }

    public void run() {
        long begin = System.currentTimeMillis();
        RLock lock = null;
        try {
            lock = RedisLock.lock("",name);
            LockThreadRequest.i ++;
            long end = System.currentTimeMillis();
            System.out.println("执行" + "-" + name + "执行时间:" + (end - begin) + "毫秒,操作i值为:" + LockThreadRequest.i );
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            RedisLock.unlock(lock);
        }
    }
}
