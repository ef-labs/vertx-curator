package com.englishtown.vertx.curator.hk2;

import com.englishtown.vertx.curator.ConfiguratorHelper;
import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorConfigurator;
import com.englishtown.vertx.curator.builders.*;
import com.englishtown.vertx.curator.builders.impl.*;
import com.englishtown.vertx.curator.impl.DefaultConfiguratorHelper;
import com.englishtown.vertx.curator.impl.DefaultCuratorClient;
import com.englishtown.vertx.curator.impl.JsonConfigCuratorConfigurator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * Curator HK2 bindings
 */
public class HK2CuratorBinder extends AbstractBinder {

    @Override
    protected void configure() {

        bind(DefaultCuratorClient.class).to(CuratorClient.class).in(Singleton.class);
        bind(DefaultConfiguratorHelper.class).to(ConfiguratorHelper.class).in(Singleton.class);
        bind(JsonConfigCuratorConfigurator.class).to(CuratorConfigurator.class).in(Singleton.class);

        // Operation builders
        bind(DefaultCuratorOperationBuilders.class).to(CuratorOperationBuilders.class).in(Singleton.class);
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
