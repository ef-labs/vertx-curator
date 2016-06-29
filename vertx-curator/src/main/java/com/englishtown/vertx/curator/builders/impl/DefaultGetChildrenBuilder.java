package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.CuratorOperation;
import com.englishtown.vertx.curator.builders.GetChildrenBuilder;
import io.vertx.core.Future;
import org.apache.curator.framework.api.CuratorWatcher;

/**
 * Default implementation of {@link com.englishtown.vertx.curator.builders.GetChildrenBuilder}
 */
public class DefaultGetChildrenBuilder extends AbstractOperationBuilder<GetChildrenBuilder> implements GetChildrenBuilder {

    @Override
    public CuratorOperation build(String path, CuratorWatcher watcher) {
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
