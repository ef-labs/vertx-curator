package com.englishtown.vertx.zookeeper.builders;

import org.apache.curator.framework.api.CuratorWatcher;

/**
 */
public interface GetDataBuilder extends ZooKeeperOperationBuilder<GetDataBuilder> {

    /**
     * Adds a {@link org.apache.zookeeper.Watcher} to the request. This Watcher will be notified
     * if the node we're getting is modified in any way.
     *
     * @param watcher
     * @return GetDataBuilder
     */
    public GetDataBuilder watcher(CuratorWatcher watcher);
}
