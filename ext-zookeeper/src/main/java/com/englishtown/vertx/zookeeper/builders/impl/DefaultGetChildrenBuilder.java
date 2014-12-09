package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.GetChildrenBuilder;
import io.vertx.core.Future;
import org.apache.curator.framework.api.CuratorWatcher;

/**
 * Default implementation of {@link com.englishtown.vertx.zookeeper.builders.GetChildrenBuilder}
 */
public class DefaultGetChildrenBuilder extends AbstractOperationBuilder<GetChildrenBuilder> implements GetChildrenBuilder {

    @Override
    public ZooKeeperOperation build(String path, CuratorWatcher watcher) {
        return (client, handler) -> {

            org.apache.curator.framework.api.GetChildrenBuilder builder = client.getCuratorFramework().getChildren();

            if (watcher != null) {
                builder.usingWatcher(client.wrapWatcher(watcher));
            }

            builder
                    .inBackground((curatorFramework, event) -> handler.handle(Future.succeededFuture(event)))
                    .forPath(path);
        };
    }

}
