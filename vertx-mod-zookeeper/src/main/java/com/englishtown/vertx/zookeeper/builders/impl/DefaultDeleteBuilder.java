package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.DeleteBuilder;
import org.vertx.java.core.impl.DefaultFutureResult;

/**
 * Default implementation of {@link com.englishtown.vertx.zookeeper.builders.impl.DefaultDeleteBuilder}
 */
public class DefaultDeleteBuilder implements DeleteBuilder {

    private boolean deletingChildrenIfNeeded;
    private boolean guaranteed;
    private Integer version;
    private String path;

    @Override
    public DeleteBuilder deletingChildrenIfNeeded() {
        deletingChildrenIfNeeded = true;
        return this;
    }

    @Override
    public DeleteBuilder guaranteed() {
        this.guaranteed = true;
        return this;
    }

    @Override
    public DeleteBuilder withVersion(int version) {
        this.version = version;
        return this;
    }

    @Override
    public DeleteBuilder forPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public ZooKeeperOperation build() {

        boolean deletingChildrenIfNeeded = this.deletingChildrenIfNeeded;
        boolean guaranteed = this.guaranteed;
        Integer version = this.version;
        String path = this.path;

        return (client, handler) -> {
            org.apache.curator.framework.api.DeleteBuilder builder = client.getCuratorFramework().delete();

            if (deletingChildrenIfNeeded) {
                builder.deletingChildrenIfNeeded();
            }
            if (guaranteed) {
                builder.guaranteed();
            }
            if (version != null) {
                builder.withVersion(version);
            }

            builder
                    .inBackground((curatorFramework, event) -> handler.handle(new DefaultFutureResult<>(event)))
                    .forPath(path);

        };
    }

}
