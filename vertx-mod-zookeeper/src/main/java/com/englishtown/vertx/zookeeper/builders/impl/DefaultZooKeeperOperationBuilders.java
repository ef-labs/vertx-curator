package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.builders.*;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 */
public class DefaultZooKeeperOperationBuilders implements ZooKeeperOperationBuilders {

    private final Provider<CreateBuilder> createBuilderProvider;
    private final Provider<GetDataBuilder> getDataBuilderProvider;
    private final Provider<GetACLBuilder> getACLBuilderProvider;
    private final Provider<GetChildrenBuilder> getChildrenBuilderProvider;
    private final Provider<DeleteBuilder> deleteBuilderProvider;

    @Inject
    public DefaultZooKeeperOperationBuilders(
            Provider<CreateBuilder> createBuilderProvider,
            Provider<GetDataBuilder> getDataBuilderProvider,
            Provider<GetACLBuilder> getACLBuilderProvider,
            Provider<GetChildrenBuilder> getChildrenBuilderProvider,
            Provider<DeleteBuilder> deleteBuilderProvider) {
        this.createBuilderProvider = createBuilderProvider;
        this.getDataBuilderProvider = getDataBuilderProvider;
        this.getACLBuilderProvider = getACLBuilderProvider;
        this.getChildrenBuilderProvider = getChildrenBuilderProvider;
        this.deleteBuilderProvider = deleteBuilderProvider;
    }

    @Override
    public CreateBuilder create() {
        return createBuilderProvider.get();
    }

    @Override
    public GetDataBuilder getData() {
        return getDataBuilderProvider.get();
    }

    @Override
    public GetACLBuilder getACL() {
        return getACLBuilderProvider.get();
    }

    @Override
    public GetChildrenBuilder getChildren() {
        return getChildrenBuilderProvider.get();
    }

    @Override
    public DeleteBuilder delete() {
        return deleteBuilderProvider.get();
    }

}
