package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.GetChildrenBuilder;
import org.apache.curator.framework.api.CuratorWatcher;
import org.vertx.java.core.impl.DefaultFutureResult;

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
                    .inBackground((curatorFramework, event) -> handler.handle(new DefaultFutureResult<>(event)))
                    .forPath(path);
        };
    }

}
