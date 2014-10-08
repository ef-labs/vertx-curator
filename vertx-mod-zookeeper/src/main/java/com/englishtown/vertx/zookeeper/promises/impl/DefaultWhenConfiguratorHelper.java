package com.englishtown.vertx.zookeeper.promises.impl;

import com.englishtown.promises.Deferred;
import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.zookeeper.ConfigElement;
import com.englishtown.vertx.zookeeper.ConfiguratorHelper;
import com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper;

import javax.inject.Inject;

/**
 * Default implementation of {@link com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper}
 */
public class DefaultWhenConfiguratorHelper implements WhenConfiguratorHelper {

    private final ConfiguratorHelper configuratorHelper;
    private final When when;

    @Inject
    public DefaultWhenConfiguratorHelper(ConfiguratorHelper configuratorHelper, When when) {
        this.configuratorHelper = configuratorHelper;
        this.when = when;
    }

    @Override
    public Promise<ConfigElement> getConfigElement(String path) {

        Deferred<ConfigElement> d = when.defer();

        configuratorHelper.getConfigElement(path, result -> {
            if (result.succeeded()) {
                d.resolve(result.result());
            } else {
                d.reject(result.cause());
            }
        });

        return d.getPromise();

    }
}
