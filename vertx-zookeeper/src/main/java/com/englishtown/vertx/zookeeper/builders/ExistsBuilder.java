package com.englishtown.vertx.zookeeper.builders;

/**
 * ZooKeeper operation builder to check a z-node existence
 */
public interface ExistsBuilder extends
        ZooKeeperOperationBuilder,
        Pathable<ExistsBuilder>,
        Watchable<ExistsBuilder> {
}
