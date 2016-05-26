package com.englishtown.vertx.curator.guice;

import com.englishtown.vertx.curator.ConfiguratorHelper;
import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorConfigurator;
import com.englishtown.vertx.curator.builders.*;
import com.englishtown.vertx.curator.builders.impl.*;
import com.englishtown.vertx.curator.impl.DefaultConfiguratorHelper;
import com.englishtown.vertx.curator.impl.DefaultCuratorClient;
import com.englishtown.vertx.curator.impl.JsonConfigCuratorConfigurator;
import com.google.inject.AbstractModule;

import javax.inject.Singleton;

/**
 * Curator Guice bindings
 */
public class GuiceCuratorBinder extends AbstractModule {

    @Override
    protected void configure() {

        bind(CuratorClient.class).to(DefaultCuratorClient.class).in(Singleton.class);
        bind(ConfiguratorHelper.class).to(DefaultConfiguratorHelper.class).in(Singleton.class);
        bind(CuratorConfigurator.class).to(JsonConfigCuratorConfigurator.class).in(Singleton.class);

        // The builders
        bind(CuratorOperationBuilders.class).to(DefaultCuratorOperationBuilders.class).in(Singleton.class);
        bind(CreateBuilder.class).to(DefaultCreateBuilder.class);
        bind(GetDataBuilder.class).to(DefaultGetDataBuilder.class);
        bind(SetDataBuilder.class).to(DefaultSetDataBuilder.class);
        bind(GetChildrenBuilder.class).to(DefaultGetChildrenBuilder.class);
        bind(GetACLBuilder.class).to(DefaultGetACLBuilder.class);
        bind(SetACLBuilder.class).to(DefaultSetACLBuilder.class);
        bind(ExistsBuilder.class).to(DefaultExistsBuilder.class);
        bind(DeleteBuilder.class).to(DefaultDeleteBuilder.class);

    }
}
