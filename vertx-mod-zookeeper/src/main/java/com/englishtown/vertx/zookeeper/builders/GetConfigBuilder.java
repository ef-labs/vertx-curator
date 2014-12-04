package com.englishtown.vertx.zookeeper.builders;

import com.englishtown.vertx.zookeeper.MatchBehavior;

/**
 */
public interface GetConfigBuilder extends
        ZooKeeperOperationBuilder,
        Pathable<GetConfigBuilder>,
        Watchable<GetConfigBuilder> {

    GetConfigBuilder environment(String environment);

    GetConfigBuilder locale(String locale);

    GetConfigBuilder application(String application);

    GetConfigBuilder matchBehavior(MatchBehavior matchBehavior);
}
