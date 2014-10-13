package com.englishtown.vertx.zookeeper.builders;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;

import java.util.List;

/**
 * ZooKeeper operation builder to create a z-node
 */
public interface CreateBuilder extends ZooKeeperOperationBuilder, Pathable<CreateBuilder> {

    CreateBuilder creatingParentsIfNeeded();

    CreateBuilder withMode(CreateMode mode);

    CreateBuilder withACL(List<ACL> aclList);

    CreateBuilder withProtection();

    CreateBuilder data(byte[] data);

}
