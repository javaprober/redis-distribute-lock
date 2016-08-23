package com.hyxt.distribute.lock;

import org.junit.Test;
import org.redisson.core.RLock;

/**
 * Created by andy on 2016/8/9.
 */

public class RedissionUseTest  {

    @Test
    public void testRedisLock (){
        try {
            long begin = System.currentTimeMillis();
            RLock lock = RedisLock.lock("ZHI", "GW");
            System.out.println("获取锁成功");
            Thread.sleep(100);
            RedisLock.unlock(lock);
            System.out.println("释放锁成功");
            long end = System.currentTimeMillis();
            System.out.println("用时:" + (end - begin));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
