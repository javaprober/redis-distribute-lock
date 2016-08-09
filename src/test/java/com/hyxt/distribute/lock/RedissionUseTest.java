package com.hyxt.distribute.lock;

import com.hyxt.distribute.lock.base.LockThreadRequest;
import com.hyxt.distribute.lock.base.RedissionBaseTest;
import org.junit.Test;
import org.redisson.core.RDeque;
import org.redisson.core.RList;
import org.redisson.core.RQueue;
import org.redisson.core.RSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by andy on 2016/8/9.
 */

public class RedissionUseTest extends RedissionBaseTest {

    @Test
    public void testPressureMutiThreadRequest() {

        List<LockThreadRequest> reqs = new ArrayList<LockThreadRequest>();
        for(int i = 0 ; i < 100; i ++) {
            String threadNo = "T" + (i%10) + "-" + i;
            LockThreadRequest request = new LockThreadRequest(threadNo,redissonClient);
            reqs.add(request);
        }
        int i = 0 ;
        for (LockThreadRequest req : reqs) {
            System.out.println("");
            req.run();
        }
        reqs = null;
    }

    @Test
    public void testRedisOperate() {
        // Set测试
        RSet<String> mySet = redissonClient.getSet("mySet");
        if (mySet != null) {
            mySet.clear();
        }
        mySet.add("1");
        mySet.add("2");
        mySet.add("3");

        RSet<String> mySetCache = redissonClient.getSet("mySet");

        for (String s : mySetCache) {
            System.out.println(s);
        }

        System.out.println("--------------------");

        // List测试
        RList<Object> myList = redissonClient.getList("myList");
        if (myList != null) {
            myList.clear();
        }

        myList.add("a");
        myList.add("b");
        myList.add("c");

        RList<Object> myListCache = redissonClient.getList("myList");

        for (Object bean : myListCache) {
            System.out.println(bean);
        }

        System.out.println("--------------------");

        //Queue测试
        RQueue<String> myQueue = redissonClient.getQueue("myQueue");
        if (myQueue != null) {
            myQueue.clear();
        }
        myQueue.add("X");
        myQueue.add("Y");
        myQueue.add("Z");

        RQueue<String> myQueueCache = redissonClient.getQueue("myQueue");

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
        RDeque<String> myDeque = redissonClient.getDeque("myDeque");
        if (myDeque != null) {
            myDeque.clear();
        }
        myDeque.add("A");
        myDeque.add("B");
        myDeque.add("C");

        RDeque<String> myDequeCache = redissonClient.getDeque("myDeque");

        Iterator<String> descendingIterator = myDequeCache.descendingIterator();

        //倒序输出
        while (descendingIterator.hasNext()) {
            System.out.println(descendingIterator.next());
        }
    }
}
