#使用说明

##maven引入方式

    <dependency>
        <groupId>com.andy</groupId>
        <artifactId>redis-lock</artifactId>
        <version>1.0.3.RELEASE</version>
    </dependency>

##使用实例

    RLock lock = null;
    try {
        //获取redis分布式锁
        //appId使用业务系统唯一标示(如:ZHI或者ZHC)
        //bizNo区分当前业务操作的最小粒度标示(如:业务表编号+userId)
        lock = RedisLock.lock(appId, bizNo);
        //------------------
        // 业务处理逻辑code...
        //这部分注意操作边界，尽量保证尽早释放锁
        //建议业务处理时间不要超过10秒
        //------------------
    } catch (InterruptedException e) {
        //锁获取异常捕获
        logger.error("exception:{}" , e);
    } finally {
        //释放锁
        RedisLock.unlock(lock);
    }

##操作变量说明:

1.	RLock获取到锁时，返回的锁对象，释放锁时unlock(RLock lock) 
2.	appNo接入应用唯一标示,比如:store/shop等。
3.	bizNo应用请求业务操作类型 比如: pay/refund等。
4.	releaseTime获取锁的自动释放时间。默认10秒
5.	waitTime获取锁的允许等待时间。 默认10秒

#zookeeper、redis(两种锁)对比

1. redis写内存IO性能远远超出zookeeper写文件
2. zookeeper获取锁原有实现方式，通过while死循环判断超时时间，即轮询的方式，这种方式是比较修改CPU性能的。
redis-lock通过netty根据lockName建立Channel异步处理超时问题，大大减少CPU的性能消耗。
3. zookeeper master单节点写入方式，也是IO的一大瓶颈，redis-redisson支持多种集群方式，足够满足后续的横向扩展。
4. 线上zookeeper作为注册中心，使用redis独立做分布式锁，进行服务拆分细化，保证zookeeper正常提供服务。

#redisson特性:

1. 提供同步和异步io
2. ConnectorWatchdog 过滤器实现断线重连
3. redission提供编码和解码
4. RedisClient提供 ChannelGroup管理实际的socket channel连接
5. redission 使用netty4.x  异步io 操作,但是通过public interface Future  来使用了同步操作。
6. 通过netty channel管理输入输出流
7. 实现中使用netty的线程池来管理创建线程。

>使用redisson解决的问题
>
1. 锁的失效设置。避免单点故障造成死锁，影响其他客户端获取锁。但是也要保证一旦一个客户端持锁，在客户端可用时不会被其他客户端解锁。
2. 支持可重入锁。
3. 减少获取锁的操作，支持申请锁有一个等待时间，而不是所有申请锁的请求要循环申请锁。
4. 支持加锁的事务或者操作粒度大小控制。
5. 支持锁超时机制，避免死锁。
6. redisson 集群模式/主从模式，提供slave和master,两种ReadMode模式，避免集群情况下，锁的高可靠性。

