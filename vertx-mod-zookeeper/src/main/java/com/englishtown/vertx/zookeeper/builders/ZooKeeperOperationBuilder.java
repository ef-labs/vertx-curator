package com.englishtown.vertx.zookeeper.builders;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;

/**
 */
public interface ZooKeeperOperationBuilder<T> {

    T forPath(String path);

    ZooKeeperOperation build();
}
