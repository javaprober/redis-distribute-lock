package com.hyxt.distribute.lock.queue;

import com.hyxt.distribute.lock.RedisLockInstance;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;

import java.util.concurrent.*;

/**
 * 排队
 * Created by apple on 16/8/12.
 */
public class RequestQueueExecutor {

//    private static Logger logger= LoggerFactory.getLogger(RequestQueueExecutor.class);
    public final static ExecutorService executor = Executors.newSingleThreadExecutor();
    private final static ConcurrentMap<String,RLock> lockMap = new ConcurrentHashMap<String,RLock>();
    private final static Integer waitTime = 10000;

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
    public final static BlockingQueue<String> queue = new LinkedBlockingDeque<>(100);
    public final static CompletionService<RLock> completionServ = new ExecutorCompletionService<RLock>(executor);
    private final static CompletionService<String> completionServReq = new ExecutorCompletionService<String>(executor);

    public static class Producer implements Runnable{
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

    public static class ConsumerReq implements Callable<String> {

        @Override
        public String call() throws Exception {
            String reqTag = null;
            try {
                reqTag = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return reqTag;
        }
    }

    public static class Consumer implements Callable<RLock> {

        public RLock call() {
            String reqTag = null;
            try {
                reqTag = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RedissonClient client = RedisLockInstance.getClient();
            RLock rLock = null;
            try {
                rLock = lockMap.get(reqTag);
                if(rLock == null) {
                    rLock = client.getLock(reqTag);
                    lockMap.put(reqTag,rLock);
                }
                boolean hasLock = rLock.tryLock(waitTime, TimeUnit.MILLISECONDS);
                if(!hasLock) {
//                    logger.error("Failed to obtain a lock ");
                }
            } catch (InterruptedException ex) {
//                logger.error("Failed to obtain a lock ,exception:{}" + ex);
            }
            return rLock;
        }
    }

    public static String getReqTag(String reqTag) {
        executor.submit(new Producer(reqTag));
//        System.out.println("线程池待处理:" + queue.size());
        Future<String> result = completionServReq.submit(new ConsumerReq());
        try {
            return result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RLock getRLock(String reqTag) {
        executor.submit(new Producer(reqTag));
//        System.out.println("线程池待处理:" + queue.size());
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

    public static void main(String[] args) {
        /*
        RequestQueueExecutor.executor.submit(new RequestQueueExecutor.Producer(lockName));
        System.out.println("线程池待处理:" + RequestQueueExecutor.queue.size());
        Future<RLock> result = RequestQueueExecutor.completionServ.submit(new RequestQueueExecutor.Consumer());
        try {
            return result.get();
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
        return lock(lockName);
        */
    }
}
