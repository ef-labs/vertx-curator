package com.englishtown.vertx.curator.builders;

/**
 * Curator operation builder factory
 */
public interface CuratorOperationBuilders {

    CreateBuilder create();

    GetDataBuilder getData();

    SetDataBuilder setData();

    GetACLBuilder getACL();

    SetACLBuilder setACL();

    GetChildrenBuilder getChildren();

    ExistsBuilder checkExists();

    DeleteBuilder delete();

}
