package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.GetDataBuilder;
import io.vertx.core.Future;
import org.apache.curator.framework.api.CuratorWatcher;

/**
 * Default implementation of {@link com.englishtown.vertx.zookeeper.builders.GetDataBuilder}
 */
public class DefaultGetDataBuilder extends AbstractOperationBuilder<GetDataBuilder> implements GetDataBuilder {

    @Override
    public ZooKeeperOperation build(String path, CuratorWatcher watcher) {
        return (client, handler) -> {
            org.apache.curator.framework.api.GetDataBuilder builder = client.getCuratorFramework().getData();

            if (watcher != null) {
                builder.usingWatcher(client.wrapWatcher(watcher));
            }

            builder
                    .inBackground((curatorFramework, event) -> handler.handle(Future.succeededFuture(event)))
                    .forPath(path);
        };
    }

}
