package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.ExistsBuilder;
import io.vertx.core.Future;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.common.PathUtils;

/**
 * Default implementation of {@link com.englishtown.vertx.zookeeper.builders.ExistsBuilder}
 */
public class DefaultExistsBuilder implements ExistsBuilder {

    private String path;
    private CuratorWatcher watcher;

    @Override
    public ExistsBuilder forPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public ExistsBuilder usingWatcher(CuratorWatcher watcher) {
        this.watcher = watcher;
        return this;
    }

    @Override
    public ZooKeeperOperation build() {

        String path = this.path;
        CuratorWatcher watcher = this.watcher;

        PathUtils.validatePath(path);

        return (client, handler) -> {
            org.apache.curator.framework.api.ExistsBuilder builder = client.getCuratorFramework().checkExists();

            if (watcher != null) {
                builder.usingWatcher(client.wrapWatcher(watcher));
            }
            builder
                    .inBackground((curatorFramework, event) -> handler.handle(Future.succeededFuture(event)))
                    .forPath(path);
        };
    }

}
