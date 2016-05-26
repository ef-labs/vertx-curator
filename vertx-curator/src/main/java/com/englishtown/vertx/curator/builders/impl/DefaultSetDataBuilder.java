package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.CuratorOperation;
import com.englishtown.vertx.curator.builders.SetDataBuilder;
import io.vertx.core.Future;
import org.apache.zookeeper.common.PathUtils;

/**
 * Default implementation of {@link com.englishtown.vertx.curator.builders.SetDataBuilder}
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
    public CuratorOperation build() {

        byte[] data = this.data;
        String path = this.path;
        Integer version = this.version;

        PathUtils.validatePath(path);

        return (client, handler) -> {
            org.apache.curator.framework.api.SetDataBuilder builder = client.getCuratorFramework().setData();

            builder.inBackground((curatorFramework, event) -> handler.handle(Future.succeededFuture(event)));

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
