package com.englishtown.vertx.zookeeper.guice;

import com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.zookeeper.promises.WhenZooKeeperClient;
import com.englishtown.vertx.zookeeper.promises.impl.DefaultWhenConfiguratorHelper;
import com.englishtown.vertx.zookeeper.promises.impl.DefaultWhenZooKeeperClient;
import com.google.inject.AbstractModule;

import javax.inject.Singleton;

/**
 * when.java Guice bindings
 */
public class GuiceWhenZooKeeperBinder extends AbstractModule {

    @Override
    protected void configure() {
        // Install the main zookeeper binder
        install(new GuiceZooKeeperBinder());

        // Promise bindings
        bind(WhenConfiguratorHelper.class).to(DefaultWhenConfiguratorHelper.class).in(Singleton.class);
        bind(WhenZooKeeperClient.class).to(DefaultWhenZooKeeperClient.class).in(Singleton.class);

    }
}
