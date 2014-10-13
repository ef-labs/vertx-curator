package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.CreateBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.util.List;

/**
 * Created by adriangonzalez on 10/13/14.
 */
public class DefaultCreateBuilder implements CreateBuilder {

    private boolean creatingParentsIfNeeded;
    private CreateMode mode;
    private List<ACL> aclList;
    private boolean withProtection;
    private byte[] data;
    private String path;

    @Override
    public CreateBuilder creatingParentsIfNeeded() {
        creatingParentsIfNeeded = true;
        return this;
    }

    @Override
    public CreateBuilder withMode(CreateMode mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public CreateBuilder withACL(List<ACL> aclList) {
        this.aclList = aclList;
        return this;
    }

    @Override
    public CreateBuilder withProtection() {
        this.withProtection = true;
        return this;
    }

    @Override
    public CreateBuilder data(byte[] data) {
        this.data = data;
        return this;
    }

    @Override
    public CreateBuilder forPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public ZooKeeperOperation build() {

        boolean creatingParentsIfNeeded = this.creatingParentsIfNeeded;
        CreateMode mode = this.mode;
        List<ACL> aclList = this.aclList;
        boolean withProtection = this.withProtection;
        byte[] data = this.data;
        String path = this.path;

        return (client, handler) -> {
            org.apache.curator.framework.api.CreateBuilder builder = client.getCuratorFramework()
                    .create();

            builder.inBackground((curatorFramework, event) -> handler.handle(new DefaultFutureResult<>(event)));

            if (creatingParentsIfNeeded) {
                builder.creatingParentsIfNeeded();
            }
            if (mode != null) {
                builder.withMode(mode);
            }
            if (aclList != null) {
                builder.withACL(aclList);
            }
            if (withProtection) {
                builder.withProtection();
            }
            if (data != null) {
                builder.forPath(path, data);
            } else {
                builder.forPath(path);
            }
        };
    }
}
