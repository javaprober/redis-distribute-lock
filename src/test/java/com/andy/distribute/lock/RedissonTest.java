package com.andy.distribute.lock;

import com.andy.distribute.lock.base.LockThreadRequest;
import com.andy.distribute.lock.base.RedissionBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.redisson.core.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by andy on 2016/8/15.
 */
public class RedissonTest extends RedissionBaseTest {


    @Test
    public void testPressureMutiThreadRequest() {

        List<LockThreadRequest> reqs = new ArrayList<LockThreadRequest>();
        for(int i = 0 ; i < 100; i ++) {
            String threadNo = "test";
            LockThreadRequest request = new LockThreadRequest(threadNo, redisson);
            reqs.add(request);
        }
        for (LockThreadRequest req : reqs) {
            req.run();
        }
//        reqs = null;
    }

    @Test
    public void testRedisOperate() {
        // Set测试
        RSet<String> mySet = redisson.getSet("mySet");
        if (mySet != null) {
            mySet.clear();
        }
        mySet.add("1");
        mySet.add("2");
        mySet.add("3");

        RSet<String> mySetCache = redisson.getSet("mySet");

        for (String s : mySetCache) {
            System.out.println(s);
        }

        System.out.println("--------------------");

        // List测试
        RList<Object> myList = redisson.getList("myList");
        if (myList != null) {
            myList.clear();
        }

        myList.add("a");
        myList.add("b");
        myList.add("c");

        RList<Object> myListCache = redisson.getList("myList");

        for (Object bean : myListCache) {
            System.out.println(bean);
        }

        System.out.println("--------------------");

        //Queue测试
        RQueue<String> myQueue = redisson.getQueue("myQueue");
        if (myQueue != null) {
            myQueue.clear();
        }
        myQueue.add("X");
        myQueue.add("Y");
        myQueue.add("Z");

        RQueue<String> myQueueCache = redisson.getQueue("myQueue");

        for (String s : myQueueCache) {
            System.out.println(s);
        }

        System.out.println("--------------------");

        System.out.println(myQueue.size());//3
        System.out.println(myQueue.poll());//X
        System.out.println(myQueue.size());//2

        System.out.println("--------------------");

        //注：虽然是从myQueue中poll的，但是由于myQueueCache与myQueue实际上是同一个缓存对象，所以下面的循环，也只剩2项
        for (String s : myQueueCache) {
            System.out.println(s);
        }
        System.out.println("--------------------");

        //Deque测试
        RDeque<String> myDeque = redisson.getDeque("myDeque");
        if (myDeque != null) {
            myDeque.clear();
        }
        myDeque.add("A");
        myDeque.add("B");
        myDeque.add("C");

        RDeque<String> myDequeCache = redisson.getDeque("myDeque");

        Iterator<String> descendingIterator = myDequeCache.descendingIterator();

        //倒序输出
        while (descendingIterator.hasNext()) {
            System.out.println(descendingIterator.next());
        }
    }

    @Test
    public void testRedissonLock() throws InterruptedException {
        RLock lock = redisson.getLock("lock");
        lock.lock();

        Thread t = new Thread() {
            public void run() {
                RLock lock = redisson.getLock("lock");
                Assert.assertTrue(lock.isLocked());
            };
        };

        t.start();
        t.join();
        lock.unlock();

        Thread t2 = new Thread() {
            public void run() {
                RLock lock = redisson.getLock("lock");
                Assert.assertFalse(lock.isLocked());
            };
        };

        t2.start();
        t2.join();
    }

    @Test
    public void lockAndUnlockTest() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程1");
                RLock lock = redisson.getLock("test1");
                System.out.println("是否已经被当前线程占用" + lock.isHeldByCurrentThread());
                if(!lock.isHeldByCurrentThread() && !lock.isLocked()) {
                    boolean tryResult = false;
                    tryResult = lock.tryLock();
                    /*try {
                        tryResult = lock.tryLock(1000,1000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        System.out.println("try lock 异常");
                        e.printStackTrace();
                    }*/
                    if (tryResult) {
                        System.out.println(Thread.currentThread().getName() +":获取锁成功");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        try {
                            lock.unlock();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else
                        System.out.println("t1获取锁失败");
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程2");
                RLock lock = redisson.getLock("test2");
                System.out.println("是否已经被当前线程占用" + lock.isHeldByCurrentThread());
                if(!lock.isHeldByCurrentThread() && !lock.isLocked()) {
                    boolean tryResult = false;
                    tryResult = lock.tryLock();
/*
                    try {
                        tryResult = lock.tryLock(1000,1000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        System.out.println("t2 try lock 异常:");
                        e.printStackTrace();
                    }
*/
                    if (tryResult) {
                        System.out.println(Thread.currentThread().getName() +":获取锁成功");
                    } else
                        System.out.println("t2获取锁失败");

                    try {
                        lock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
