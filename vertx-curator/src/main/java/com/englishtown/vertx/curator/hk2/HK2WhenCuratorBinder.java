package com.englishtown.vertx.curator.hk2;

import com.englishtown.vertx.curator.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.curator.promises.WhenCuratorClient;
import com.englishtown.vertx.curator.promises.impl.DefaultWhenConfiguratorHelper;
import com.englishtown.vertx.curator.promises.impl.DefaultWhenCuratorClient;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

/**
 * when.java HK2 bindings
 */
public class HK2WhenCuratorBinder extends AbstractBinder {

    @Override
    protected void configure() {
        // Install the main curator binder
        install(new HK2CuratorBinder());

        // Promise bindings
        bind(DefaultWhenConfiguratorHelper.class).to(WhenConfiguratorHelper.class).in(Singleton.class);
        bind(DefaultWhenCuratorClient.class).to(WhenCuratorClient.class).in(Singleton.class);

    }
}
