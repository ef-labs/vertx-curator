package com.englishtown.vertx.zookeeper.integration.guice;

import com.englishtown.vertx.zookeeper.integration.AbstractZooKeeperClientIntegrationTest;
import com.englishtown.vertx.zookeeper.integration.Utils;
import com.google.inject.Injector;

/**
 * Integration tests for {@link com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders} and {@link com.englishtown.vertx.zookeeper.ZooKeeperClient}
 */
public class GuiceZooKeeperClientIntegrationTest extends AbstractZooKeeperClientIntegrationTest {

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
