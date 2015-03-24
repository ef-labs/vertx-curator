package com.englishtown.vertx.zookeeper.integration.hk2;

import com.englishtown.vertx.zookeeper.integration.AbstractZooKeeperClientIntegrationTest;
import com.englishtown.vertx.zookeeper.integration.Utils;
import org.glassfish.hk2.api.ServiceLocator;

/**
 * HK2 client integration tests
 */
public class HK2ZooKeeperClientIntegrationTest extends AbstractZooKeeperClientIntegrationTest {

    private ServiceLocator locator;

    @Override
    protected void initLocator() {
        locator = Utils.createLocator(vertx);
    }

    @Override
    protected <T> T getService(Class<T> clazz) {
        return locator.getService(clazz);
    }
}
