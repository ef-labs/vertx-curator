package com.englishtown.vertx.zookeeper.hk2;

import com.englishtown.promises.hk2.WhenBinder;
import com.englishtown.vertx.zookeeper.ConfiguratorClient;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.builders.GetDataBuilder;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.builders.impl.DefaultGetDataBuilder;
import com.englishtown.vertx.zookeeper.builders.impl.DefaultZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.impl.DefaultConfiguratorClient;
import com.englishtown.vertx.zookeeper.impl.DefaultZooKeeperClient;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 */
public class ZooKeeperBinder extends AbstractBinder {

    @Override
    protected void configure() {
        install(new WhenBinder());

        bind(DefaultZooKeeperClient.class).to(ZooKeeperClient.class).in(Singleton.class);
        bind(DefaultConfiguratorClient.class).to(ConfiguratorClient.class).in(Singleton.class);

        // The builders
        bind(DefaultZooKeeperOperationBuilders.class).to(ZooKeeperOperationBuilders.class).in(Singleton.class);
        bind(DefaultGetDataBuilder.class).to(GetDataBuilder.class);
    }
}
