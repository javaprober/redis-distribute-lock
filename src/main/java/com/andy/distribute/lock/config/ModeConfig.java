package com.andy.distribute.lock.config;

/**
 * redis 连接配置
 * Created by andy on 2016/8/10.
 */
public interface ModeConfig {
    //配置路径头
    public static final String PRE_CONFIG = "";

    //cluster mode
    public static final String CLUSTER_MODE = "clusterServersConfig.yml";

    //master slave mode
    public static final String MASTER_SLAVE_MODE = "masterSlaveServersConfig.yml";

    //sentinel mode
    public static final String SENTINEL_MODE = "sentinelServersConfig.yml";

    //elasticache mode
    public static final String ELASTICACHE_MODE = "elasticacheServersConfig.yml";

    //single mode
    public static final String SINGLE_MODE = "singleServerConfig.yml";

    //base config
    public static final String BASE_CONFIG ="base_config";

}
