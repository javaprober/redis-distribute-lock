package com.hyxt.distribute.lock.client;

import com.hyxt.distribute.lock.config.ModeConfig;
import com.hyxt.distribute.lock.exception.RedisLockException;
import com.hyxt.distribute.lock.util.FilePathUtil;
import com.hyxt.distribute.lock.util.InternMap;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * redis client instance create
 * Created by andy on 2016/8/10.
 */
public interface RedisClientHandler {

    class Factory {

        private Factory() {

        }

        private static class FacotryHandler {
            static Factory instance = new Factory();
        }

        public static Factory getInstance() {
            return FacotryHandler.instance;
        }

        ClusterMode mode = ClusterMode.SINGLE_INSTANCE;

        static Map<ClusterMode ,String> modeConfigPath = new HashMap<ClusterMode ,String>();
        static  {
            modeConfigPath.put(ClusterMode.CLUSTER , ModeConfig.PRE_CONFIG + ModeConfig.CLUSTER_MODE);
            modeConfigPath.put(ClusterMode.ELASTICACHE , ModeConfig.PRE_CONFIG + ModeConfig.ELASTICACHE_MODE);
            modeConfigPath.put(ClusterMode.SINGLE_INSTANCE , ModeConfig.PRE_CONFIG + ModeConfig.SINGLE_MODE);
            modeConfigPath.put(ClusterMode.SENTINEL , ModeConfig.PRE_CONFIG + ModeConfig.SENTINEL_MODE);
            modeConfigPath.put(ClusterMode.MASTER_SLAVE , ModeConfig.PRE_CONFIG + ModeConfig.MASTER_SLAVE_MODE);
        }
        private static Logger logger = LoggerFactory.getLogger(Factory.class);
        private RedissonClient redissonClient;

        // used to intern instances so we don't keep re-creating them millions of times for the same key
        private final InternMap<ClusterMode, RedissonClient> intern
                = new InternMap<ClusterMode, RedissonClient>(
                new InternMap.ValueConstructor<ClusterMode, RedissonClient>() {
                    public RedissonClient create(ClusterMode key) throws RedisLockException {
                        if(key != null) {
                            return buildRedissonClient();
                        }
                        return null;
                    }
                });

        public RedissonClient asMode() throws RedisLockException {
            //single单机模式
            return intern.interned(mode);
        }


        public RedissonClient buildRedissonClient() throws RedisLockException {
            try {
                String configFilePath = modeConfigPath.get(mode);
                InputStream inputStream = FilePathUtil.getFile(configFilePath);
                Config config = new Config(Config.fromYAML(inputStream));
                redissonClient = Redisson.create(config);
                if(redissonClient == null) {
                    throw new RedisLockException("redis lock create connection faild,Please check redis address or the number of connections beyond.");
                }
            } catch (IOException e) {
                throw new RedisLockException("Error reading configuration file:" + e.getMessage());
//                logger.error("Connect redis server fail, exception:{}",e);
            } /*finally {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        if (redissonClient != null) {
                            redissonClient.shutdown();
                            logger.info("关闭Redis Server连接");
                        }
                    }
                });
            }*/
            return redissonClient;
        }
    }
}
