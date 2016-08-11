package com.hyxt.distribute.lock;

/**
 * Created by andy on 2016/8/10.
 */
public enum ClusterMode {
    CLUSTER,                //多节点集群
    ELASTICACHE,            //支持ElastiCache redis管理方式
    SINGLE_INSTANCE,       //redis单实例
    SENTINEL,               //redis Sentinel:提供监控、提醒、自动故障迁移
    MASTER_SLAVE           //主从模式
}
