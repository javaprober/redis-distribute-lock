package com.hyxt.distribute.lock.client;

/**
 * redis client instance create
 * Created by andy on 2016/8/10.
 */
public interface RedisClientStaticHandler {

    /*class Factory {

        static ClusterMode mode = ClusterMode.SINGLE_INSTANCE;

        static Map<ClusterMode ,String> modeConfigPath = new HashMap<ClusterMode ,String>();
        static  {
            modeConfigPath.put(ClusterMode.CLUSTER , ModeConfig.PRE_CONFIG + ModeConfig.CLUSTER_MODE);
            modeConfigPath.put(ClusterMode.ELASTICACHE , ModeConfig.PRE_CONFIG + ModeConfig.ELASTICACHE_MODE);
            modeConfigPath.put(ClusterMode.SINGLE_INSTANCE , ModeConfig.PRE_CONFIG + ModeConfig.SINGLE_MODE);
            modeConfigPath.put(ClusterMode.SENTINEL , ModeConfig.PRE_CONFIG + ModeConfig.SENTINEL_MODE);
            modeConfigPath.put(ClusterMode.MASTER_SLAVE , ModeConfig.PRE_CONFIG + ModeConfig.MASTER_SLAVE_MODE);
        }
        private static Logger logger = LoggerFactory.getLogger(Factory.class);
        public static RedissonClient redissonClient;
        static {
            try {
                String configFilePath = modeConfigPath.get(mode);
                File file = FilePathUtil.getFile(configFilePath);
                Config config = new Config(Config.fromYAML(file));
                synchronized (Factory.class) {
                    redissonClient = Redisson.create(config);
                }
                logger.info("成功连接Redis Server");
            } catch (IOException e) {
                logger.error("连接Redis Server失败" + e);
            } finally {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        if (redissonClient != null) {
                            redissonClient.shutdown();
                            logger.info("关闭Redis Server连接");
                        }
                    }
                });
            }
        }
    }*/
}
