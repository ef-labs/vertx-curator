package com.englishtown.vertx.curator.integration.hk2;

import com.englishtown.vertx.curator.integration.AbstractConfiguratorHelperIntegrationTest;
import com.englishtown.vertx.curator.integration.Utils;
import org.glassfish.hk2.api.ServiceLocator;

/**
 * HK2 configuration helper integration test
 */
public class HK2ConfiguratorHelperIntegrationTest extends AbstractConfiguratorHelperIntegrationTest {

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
