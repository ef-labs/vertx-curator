package com.englishtown.vertx.curator.builders;

import org.apache.zookeeper.CreateMode;

/**
 * Curator operation builder to create a z-node
 */
public interface CreateBuilder extends
        CuratorOperationBuilder,
        Pathable<CreateBuilder>,
        ACLable<CreateBuilder>,
        Dataable<CreateBuilder> {

    CreateBuilder creatingParentsIfNeeded();

    CreateBuilder withMode(CreateMode mode);

    CreateBuilder withProtection();

}
