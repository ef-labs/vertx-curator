package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.builders.GetDataBuilder;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 */
public class DefaultZooKeeperOperationBuilders implements ZooKeeperOperationBuilders {

    private Provider<GetDataBuilder> getDataBuilderProvider;

    @Inject
    public DefaultZooKeeperOperationBuilders(Provider<GetDataBuilder> getDataBuilderProvider) {
        this.getDataBuilderProvider = getDataBuilderProvider;
    }

    @Override
    public GetDataBuilder getData() {
        return getDataBuilderProvider.get();
    }
}
