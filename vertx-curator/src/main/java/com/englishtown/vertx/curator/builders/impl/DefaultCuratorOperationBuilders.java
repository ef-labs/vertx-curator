package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.builders.*;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 */
public class DefaultCuratorOperationBuilders implements CuratorOperationBuilders {

    private final Provider<CreateBuilder> createBuilderProvider;
    private final Provider<GetDataBuilder> getDataBuilderProvider;
    private final Provider<SetDataBuilder> setDataBuilderProvider;
    private final Provider<GetACLBuilder> getACLBuilderProvider;
    private final Provider<SetACLBuilder> setACLBuilderProvider;
    private final Provider<GetChildrenBuilder> getChildrenBuilderProvider;
    private final Provider<ExistsBuilder> existsBuilderProvider;
    private final Provider<DeleteBuilder> deleteBuilderProvider;

    @Inject
    public DefaultCuratorOperationBuilders(
            Provider<CreateBuilder> createBuilderProvider,
            Provider<GetDataBuilder> getDataBuilderProvider,
            Provider<SetDataBuilder> setDataBuilderProvider,
            Provider<GetACLBuilder> getACLBuilderProvider,
            Provider<SetACLBuilder> setACLBuilderProvider,
            Provider<GetChildrenBuilder> getChildrenBuilderProvider,
            Provider<ExistsBuilder> existsBuilderProvider, Provider<DeleteBuilder> deleteBuilderProvider) {
        this.createBuilderProvider = createBuilderProvider;
        this.getDataBuilderProvider = getDataBuilderProvider;
        this.setDataBuilderProvider = setDataBuilderProvider;
        this.getACLBuilderProvider = getACLBuilderProvider;
        this.setACLBuilderProvider = setACLBuilderProvider;
        this.getChildrenBuilderProvider = getChildrenBuilderProvider;
        this.existsBuilderProvider = existsBuilderProvider;
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

}
