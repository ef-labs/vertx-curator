package com.englishtown.vertx.curator.integration;

import com.englishtown.vertx.guice.GuiceVertxBinder;
import com.englishtown.vertx.hk2.HK2VertxBinder;
import com.englishtown.vertx.promises.guice.GuiceWhenBinder;
import com.englishtown.vertx.promises.hk2.HK2WhenBinder;
import com.englishtown.vertx.curator.guice.GuiceWhenCuratorBinder;
import com.englishtown.vertx.curator.hk2.HK2WhenCuratorBinder;
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
                new HK2WhenCuratorBinder(),
                new HK2WhenBinder(),
                new HK2VertxBinder(vertx));

        return locator;
    }

    public static Injector createInjector(Vertx vertx) {

        return Guice.createInjector(
                new GuiceWhenCuratorBinder(),
                new GuiceWhenBinder(),
                new GuiceVertxBinder(vertx));

    }
}
