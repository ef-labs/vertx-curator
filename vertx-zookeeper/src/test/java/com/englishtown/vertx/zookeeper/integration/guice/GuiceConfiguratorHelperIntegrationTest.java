package com.englishtown.vertx.zookeeper.integration.guice;

import com.englishtown.vertx.zookeeper.integration.AbstractConfiguratorHelperIntegrationTest;
import com.englishtown.vertx.zookeeper.integration.AbstractZooKeeperClientIntegrationTest;
import com.englishtown.vertx.zookeeper.integration.Utils;
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
