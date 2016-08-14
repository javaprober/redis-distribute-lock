package com.hyxt.distribute.lock.queue;

import com.hyxt.distribute.lock.RedisLockInstance;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;

import java.util.concurrent.*;

/**
 * Created by apple on 16/8/12.
 */
public class RequestQueueExecutor {
    private static ExecutorService executor = Executors.newCachedThreadPool();
//    private static ExecutorService executor = Executors.newFixedThreadPool(10000);
    /*static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("关闭线程池");
                executor.shutdown();
            }
        }));
    }*/
    private static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10000);
    private static CompletionService<RLock> completionServ = new ExecutorCompletionService<RLock>(executor);

    private static class Producer implements Runnable{
        String reqTag;

        public Producer(String reqTag){
            this.reqTag = reqTag;
        }

        public void run() {
            try {
                queue.put(reqTag);
            } catch (InterruptedException ex) {

            }
        }
    }

    private static class Consumer implements Callable<RLock> {

        public RLock call() {
            RedissonClient client = RedisLockInstance.getClient();
            RLock lock = null;
            try {
                String name = queue.take();
                lock = client.getLock(name);
                lock.lock(10, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
            }
            return lock;
        }
    }

    public static RLock getRLock(String reqTag) {
        executor.submit(new Producer(reqTag));
        System.out.println("线程池待处理:" + queue.size());
        Future<RLock> result = completionServ.submit(new Consumer());
        try {
            return result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
