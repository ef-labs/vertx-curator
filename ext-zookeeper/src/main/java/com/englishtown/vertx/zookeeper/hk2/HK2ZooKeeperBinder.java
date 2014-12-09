package com.englishtown.vertx.zookeeper.hk2;

import com.englishtown.vertx.zookeeper.ConfiguratorHelper;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import com.englishtown.vertx.zookeeper.builders.*;
import com.englishtown.vertx.zookeeper.builders.impl.*;
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

        // Operation builders
        bind(DefaultZooKeeperOperationBuilders.class).to(ZooKeeperOperationBuilders.class).in(Singleton.class);
        bind(DefaultCreateBuilder.class).to(CreateBuilder.class);
        bind(DefaultGetDataBuilder.class).to(GetDataBuilder.class);
        bind(DefaultSetDataBuilder.class).to(SetDataBuilder.class);
        bind(DefaultGetChildrenBuilder.class).to(GetChildrenBuilder.class);
        bind(DefaultGetACLBuilder.class).to(GetACLBuilder.class);
        bind(DefaultSetACLBuilder.class).to(SetACLBuilder.class);
        bind(DefaultExistsBuilder.class).to(ExistsBuilder.class);
        bind(DefaultDeleteBuilder.class).to(DeleteBuilder.class);

    }
}
