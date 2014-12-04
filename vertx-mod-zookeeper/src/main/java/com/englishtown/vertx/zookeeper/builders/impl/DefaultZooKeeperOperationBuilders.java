package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.builders.*;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 */
public class DefaultZooKeeperOperationBuilders implements ZooKeeperOperationBuilders {

    private final Provider<CreateBuilder> createBuilderProvider;
    private final Provider<GetDataBuilder> getDataBuilderProvider;
    private final Provider<SetDataBuilder> setDataBuilderProvider;
    private final Provider<GetACLBuilder> getACLBuilderProvider;
    private final Provider<SetACLBuilder> setACLBuilderProvider;
    private final Provider<GetChildrenBuilder> getChildrenBuilderProvider;
    private final Provider<ExistsBuilder> existsBuilderProvider;
    private final Provider<DeleteBuilder> deleteBuilderProvider;
    private final Provider<GetConfigBuilder> getConfigBuilderProvider;

    @Inject
    public DefaultZooKeeperOperationBuilders(
            Provider<CreateBuilder> createBuilderProvider,
            Provider<GetDataBuilder> getDataBuilderProvider,
            Provider<SetDataBuilder> setDataBuilderProvider,
            Provider<GetACLBuilder> getACLBuilderProvider,
            Provider<SetACLBuilder> setACLBuilderProvider,
            Provider<GetChildrenBuilder> getChildrenBuilderProvider,
            Provider<ExistsBuilder> existsBuilderProvider,
            Provider<DeleteBuilder> deleteBuilderProvider,
            Provider<GetConfigBuilder> getConfigBuilderProvider) {
        this.createBuilderProvider = createBuilderProvider;
        this.getDataBuilderProvider = getDataBuilderProvider;
        this.setDataBuilderProvider = setDataBuilderProvider;
        this.getACLBuilderProvider = getACLBuilderProvider;
        this.setACLBuilderProvider = setACLBuilderProvider;
        this.getChildrenBuilderProvider = getChildrenBuilderProvider;
        this.existsBuilderProvider = existsBuilderProvider;
        this.deleteBuilderProvider = deleteBuilderProvider;
        this.getConfigBuilderProvider = getConfigBuilderProvider;
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
    public SetDataBuilder setData() {
        return setDataBuilderProvider.get();
    }

    @Override
    public GetACLBuilder getACL() {
        return getACLBuilderProvider.get();
    }

    @Override
    public SetACLBuilder setACL() {
        return setACLBuilderProvider.get();
    }

    @Override
    public GetChildrenBuilder getChildren() {
        return getChildrenBuilderProvider.get();
    }

    @Override
    public ExistsBuilder checkExists() {
        return existsBuilderProvider.get();
    }

    @Override
    public DeleteBuilder delete() {
        return deleteBuilderProvider.get();
    }

    @Override
    public GetConfigBuilder getConfig() {
        return getConfigBuilderProvider.get();
    }
}
