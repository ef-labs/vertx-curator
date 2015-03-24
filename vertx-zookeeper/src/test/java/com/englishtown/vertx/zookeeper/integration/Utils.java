package com.englishtown.vertx.zookeeper.integration;

import com.englishtown.vertx.guice.GuiceVertxBinder;
import com.englishtown.vertx.hk2.HK2VertxBinder;
import com.englishtown.vertx.promises.guice.GuiceWhenBinder;
import com.englishtown.vertx.promises.hk2.HK2WhenBinder;
import com.englishtown.vertx.zookeeper.guice.GuiceWhenZooKeeperBinder;
import com.englishtown.vertx.zookeeper.hk2.HK2WhenZooKeeperBinder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * Integration test utils
 */
public class Utils {

    public static ServiceLocator createLocator(Vertx vertx) {

        ServiceLocator locator = ServiceLocatorFactory.getInstance().create(null);

        ServiceLocatorUtilities.bind(locator,
                new HK2WhenZooKeeperBinder(),
                new HK2WhenBinder(),
                new HK2VertxBinder(vertx));

        return locator;
    }

    public static Injector createInjector(Vertx vertx) {

        return Guice.createInjector(
                new GuiceWhenZooKeeperBinder(),
                new GuiceWhenBinder(),
                new GuiceVertxBinder(vertx));

    }
}
