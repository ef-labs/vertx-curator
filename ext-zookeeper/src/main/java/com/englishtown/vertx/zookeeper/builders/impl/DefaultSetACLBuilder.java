package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.SetACLBuilder;
import io.vertx.core.Future;
import org.apache.zookeeper.common.PathUtils;
import org.apache.zookeeper.data.ACL;

import java.util.List;

/**
 * Default implementation of {@link com.englishtown.vertx.zookeeper.builders.SetACLBuilder}
 */
public class DefaultSetACLBuilder implements SetACLBuilder {

    private Integer version;
    private List<ACL> aclList;
    private String path;

    @Override
    public SetACLBuilder forPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public SetACLBuilder withVersion(int version) {
        this.version = version;
        return this;
    }

    @Override
    public SetACLBuilder withACL(List<ACL> aclList) {
        this.aclList = aclList;
        return this;
    }

    @Override
    public ZooKeeperOperation build() {

        Integer version = this.version;
        List<ACL> aclList = this.aclList;
        String path = this.path;

        PathUtils.validatePath(path);

        return (client, handler) -> {
            org.apache.curator.framework.api.SetACLBuilder builder = client.getCuratorFramework().setACL();

            if (version != null) {
                builder.withVersion(version);
            }

            builder
                    .withACL(aclList)
                    .inBackground((curatorFramework, event) -> handler.handle(Future.succeededFuture(event)))
                    .forPath(path);

        };
    }

}
