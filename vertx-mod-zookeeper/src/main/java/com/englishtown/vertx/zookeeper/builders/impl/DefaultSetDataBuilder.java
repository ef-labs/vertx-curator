package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.SetDataBuilder;
import org.vertx.java.core.impl.DefaultFutureResult;

/**
 * Default implementation of {@link com.englishtown.vertx.zookeeper.builders.SetDataBuilder}
 */
public class DefaultSetDataBuilder implements SetDataBuilder {

    private byte[] data;
    private String path;
    private Integer version;

    @Override
    public SetDataBuilder data(byte[] data) {
        this.data = data;
        return this;
    }

    @Override
    public SetDataBuilder forPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public SetDataBuilder withVersion(int version) {
        this.version = version;
        return this;
    }

    @Override
    public ZooKeeperOperation build() {

        byte[] data = this.data;
        String path = this.path;
        Integer version = this.version;

        return (client, handler) -> {
            org.apache.curator.framework.api.SetDataBuilder builder = client.getCuratorFramework().setData();

            builder.inBackground((curatorFramework, event) -> handler.handle(new DefaultFutureResult<>(event)));

            if (version != null) {
                builder.withVersion(version);
            }
            if (data != null) {
                builder.forPath(path, data);
            } else {
                builder.forPath(path);
            }

        };
    }
}
