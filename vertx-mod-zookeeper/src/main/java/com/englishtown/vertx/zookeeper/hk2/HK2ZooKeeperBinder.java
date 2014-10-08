package com.englishtown.vertx.zookeeper.hk2;

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
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * Zookeeper HK2 bindings
 */
public class HK2ZooKeeperBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bind(DefaultZooKeeperClient.class).to(ZooKeeperClient.class).in(Singleton.class);
        bind(DefaultConfiguratorHelper.class).to(ConfiguratorHelper.class).in(Singleton.class);
        bind(JsonConfigZooKeeperConfigurator.class).to(ZooKeeperConfigurator.class).in(Singleton.class);

        // The builders
        bind(DefaultZooKeeperOperationBuilders.class).to(ZooKeeperOperationBuilders.class).in(Singleton.class);
        bind(DefaultGetDataBuilder.class).to(GetDataBuilder.class);
    }
}
