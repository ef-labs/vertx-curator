package com.englishtown.vertx.zookeeper.builders;

/**
 * ZooKeeper operation builder factory
 */
public interface ZooKeeperOperationBuilders {

    CreateBuilder create();

    GetDataBuilder getData();

    SetDataBuilder setData();

    GetACLBuilder getACL();

    SetACLBuilder setACL();

    GetChildrenBuilder getChildren();

    ExistsBuilder checkExists();

    DeleteBuilder delete();

}
