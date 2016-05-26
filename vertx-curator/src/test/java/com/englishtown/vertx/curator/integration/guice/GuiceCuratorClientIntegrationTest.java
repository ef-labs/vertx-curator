package com.englishtown.vertx.curator.integration.guice;

import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.builders.CuratorOperationBuilders;
import com.englishtown.vertx.curator.integration.AbstractCuratorClientIntegrationTest;
import com.englishtown.vertx.curator.integration.Utils;
import com.google.inject.Injector;

/**
 * Integration tests for {@link CuratorOperationBuilders} and {@link CuratorClient}
 */
public class GuiceCuratorClientIntegrationTest extends AbstractCuratorClientIntegrationTest {

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
