package com.englishtown.vertx.zookeeper.guice;

import com.englishtown.vertx.zookeeper.ConfiguratorHelper;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import com.englishtown.vertx.zookeeper.builders.GetDataBuilder;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.builders.impl.DefaultGetDataBuilder;
import com.englishtown.vertx.zookeeper.builders.impl.DefaultZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.impl.DefaultConfiguratorHelper;
import com.englishtown.vertx.zookeeper.impl.DefaultZooKeeperClient;
import com.englishtown.vertx.zookeeper.impl.JsonConfigZooKeeperConfigurator;
import com.google.inject.AbstractModule;

import javax.inject.Singleton;

/**
 * Zookeeper Guice bindings
 */
public class GuiceZooKeeperBinder extends AbstractModule {

    @Override
    protected void configure() {

        bind(ZooKeeperClient.class).to(DefaultZooKeeperClient.class).in(Singleton.class);
        bind(ConfiguratorHelper.class).to(DefaultConfiguratorHelper.class).in(Singleton.class);
        bind(ZooKeeperConfigurator.class).to(JsonConfigZooKeeperConfigurator.class).in(Singleton.class);

        // The builders
        bind(ZooKeeperOperationBuilders.class).to(DefaultZooKeeperOperationBuilders.class).in(Singleton.class);
        bind(GetDataBuilder.class).to(DefaultGetDataBuilder.class);

    }
}
