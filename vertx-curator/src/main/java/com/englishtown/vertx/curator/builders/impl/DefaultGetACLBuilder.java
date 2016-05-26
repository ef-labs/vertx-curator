package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.CuratorOperation;
import com.englishtown.vertx.curator.builders.GetACLBuilder;
import io.vertx.core.Future;
import org.apache.zookeeper.common.PathUtils;

/**
 * Default implementation of {@link com.englishtown.vertx.curator.builders.GetACLBuilder}
 */
public class DefaultGetACLBuilder implements GetACLBuilder {

    private String path;

    @Override
    public GetACLBuilder forPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public CuratorOperation build() {

        String path = this.path;

        PathUtils.validatePath(path);

        return (client, handler) -> {
            client.getCuratorFramework()
                    .getACL()
                    .inBackground((curatorFramework, event) -> handler.handle(Future.succeededFuture(event)))
                    .forPath(path);
        };
    }
}
