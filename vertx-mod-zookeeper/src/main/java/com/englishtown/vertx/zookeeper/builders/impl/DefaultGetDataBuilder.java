package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.builders.GetDataBuilder;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.api.CuratorWatcher;
import org.vertx.java.core.impl.DefaultFutureResult;

/**
 */
public class DefaultGetDataBuilder implements GetDataBuilder {

    private String path;
    private CuratorWatcher watcher;

    @Override
    public GetDataBuilder forPath(String path) {
        this.path = path;

        return this;
    }

    @Override
    public GetDataBuilder watcher(CuratorWatcher watcher) {
        this.watcher = watcher;

        return this;
    }

    @Override
    public ZooKeeperOperation build() {
        final String path = this.path;
        final CuratorWatcher watcher = this.watcher;

        return (client, handler) -> client.getCuratorFramework().getData()
                .usingWatcher(watcher)
                .inBackground((curatorFramework, event) -> handler.handle(new DefaultFutureResult<>(event)))
                .forPath(path);
    }
}
