package com.englishtown.vertx.zookeeper.builders;

/**
 * ZooKeeper operation builder to set data
 */
public interface SetDataBuilder extends
        ZooKeeperOperationBuilder,
        Pathable<SetDataBuilder>,
        Dataable<SetDataBuilder>,
        Versionable<SetDataBuilder> {
}
