package com.englishtown.vertx.zookeeper.builders;

/**
 * ZooKeeper operation builder to get data
 */
public interface GetDataBuilder extends
        ZooKeeperOperationBuilder,
        Pathable<GetDataBuilder>,
        Watchable<GetDataBuilder> {

}
