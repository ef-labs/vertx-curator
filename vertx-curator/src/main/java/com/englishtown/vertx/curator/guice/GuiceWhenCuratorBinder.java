package com.englishtown.vertx.curator.guice;

import com.englishtown.vertx.curator.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.curator.promises.WhenCuratorClient;
import com.englishtown.vertx.curator.promises.impl.DefaultWhenConfiguratorHelper;
import com.englishtown.vertx.curator.promises.impl.DefaultWhenCuratorClient;
import com.google.inject.AbstractModule;

import javax.inject.Singleton;

/**
 * when.java Guice bindings
 */
public class GuiceWhenCuratorBinder extends AbstractModule {

    @Override
    protected void configure() {
        // Install the main curator binder
        install(new GuiceCuratorBinder());

        // Promise bindings
        bind(WhenConfiguratorHelper.class).to(DefaultWhenConfiguratorHelper.class).in(Singleton.class);
        bind(WhenCuratorClient.class).to(DefaultWhenCuratorClient.class).in(Singleton.class);

    }
}
