package com.hyxt.distribute.lock.queue;

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
    private static BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
    private static CompletionService<String> completionServ = new ExecutorCompletionService<>(executor);

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

    private static class Consumer implements Callable<String> {

        public String call() throws InterruptedException {
            try {
                String name = queue.take();
                return name;
            } catch (InterruptedException ex) {
                throw ex;
            }

        }
    }

    public static String getName(String reqTag) {
        executor.submit(new Producer(reqTag));
        System.out.println("线程池待处理:" + queue.size());
        try {
            Future<String> result = completionServ.submit(new Consumer());
            return result.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
