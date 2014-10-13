package com.englishtown.vertx.zookeeper.builders;

/**
 * ZooKeeper operation builder to delete a z-node
 */
public interface DeleteBuilder extends ZooKeeperOperationBuilder, Pathable<DeleteBuilder> {

    DeleteBuilder deletingChildrenIfNeeded();

    DeleteBuilder guaranteed();

    DeleteBuilder withVersion(int version);

}
