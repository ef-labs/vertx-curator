package com.englishtown.vertx.zookeeper.builders;

/**
 */
public interface ZooKeeperOperationBuilders {

    CreateBuilder create();

    GetDataBuilder getData();

    GetACLBuilder getACL();

    GetChildrenBuilder getChildren();

    DeleteBuilder delete();

}
