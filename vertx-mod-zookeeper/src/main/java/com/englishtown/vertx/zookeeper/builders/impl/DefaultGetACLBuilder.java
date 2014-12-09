package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.GetACLBuilder;
import org.apache.zookeeper.common.PathUtils;
import org.vertx.java.core.impl.DefaultFutureResult;

/**
 * Default implementation of {@link com.englishtown.vertx.zookeeper.builders.GetACLBuilder}
 */
public class DefaultGetACLBuilder implements GetACLBuilder {

    private String path;

    @Override
    public GetACLBuilder forPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public ZooKeeperOperation build() {

        String path = this.path;

        PathUtils.validatePath(path);

        return (client, handler) -> {
            client.getCuratorFramework()
                    .getACL()
                    .inBackground((curatorFramework, event) -> handler.handle(new DefaultFutureResult<>(event)))
                    .forPath(path);
        };
    }
}
