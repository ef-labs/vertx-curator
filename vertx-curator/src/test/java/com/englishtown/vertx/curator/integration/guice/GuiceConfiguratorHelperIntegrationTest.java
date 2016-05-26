package com.englishtown.vertx.curator.integration.guice;

import com.englishtown.vertx.curator.integration.AbstractConfiguratorHelperIntegrationTest;
import com.englishtown.vertx.curator.integration.Utils;
import com.google.inject.Injector;

/**
 */
public class GuiceConfiguratorHelperIntegrationTest extends AbstractConfiguratorHelperIntegrationTest {

    private Injector injector;

    @Override
    protected void initLocator() {
        injector = Utils.createInjector(vertx);
    }

    @Override
    protected <T> T getService(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}
