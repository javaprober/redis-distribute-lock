package com.andy.distribute.lock.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by andy on 2016/8/24.
 */
public class LockTest {
    final static int threads = 2000000;
    static CountDownLatch latch = new CountDownLatch(threads);
    static Map<String,Integer> map = new HashMap<String,Integer>();

    public static void counter(String name) {
        synchronized(name) {
            Integer c = map.get(name);
            if(c == null) {
                c = 1;

            } else {
                c = c + 1;
            }
            map.put(name,c);
            System.out.println(new N(name,c));
        }
    }

    public synchronized static void counter1(String name) {
        Integer c = map.get(name);
        if(c == null) {
            c = 1;

        } else {
            c = c + 1;
        }
        map.put(name,c);
        System.out.println(new N(name,c));
    }

    public static void main(String[] args) throws InterruptedException {
        long beginTime = System.currentTimeMillis();
        final LockTest lockTest = new LockTest();
        for (int i = 0 ; i < threads/2 ; i ++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    lockTest.counter("a");
                    latch.countDown();
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    lockTest.counter("b");
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("用时:" + (endTime - beginTime));
    }
    static class N {
        String name;
        int counter;
        N(String name ,int counter) {
            this.name = name;
            this.counter = counter;
        }
        @Override
        public String toString() {
            return "N{" +
                    "name='" + name + '\'' +
                    ", counter=" + counter +
                    '}';
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }
    }
}
