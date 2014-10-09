package com.englishtown.vertx.zookeeper.hk2;

import com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.zookeeper.promises.impl.DefaultWhenConfiguratorHelper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * when.java HK2 bindings
 */
public class HK2WhenZooKeeperBinder extends AbstractBinder {

    @Override
    protected void configure() {
        // Install the main zookeeper binder
        install(new HK2ZooKeeperBinder());

        // Promise bindings
        bind(DefaultWhenConfiguratorHelper.class).to(WhenConfiguratorHelper.class).in(Singleton.class);

    }
}
