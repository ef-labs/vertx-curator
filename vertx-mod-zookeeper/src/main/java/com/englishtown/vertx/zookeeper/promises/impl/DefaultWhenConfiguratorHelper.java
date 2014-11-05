package com.englishtown.vertx.zookeeper.promises.impl;

import com.englishtown.promises.Deferred;
import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.zookeeper.ConfigElement;
import com.englishtown.vertx.zookeeper.ConfiguratorHelper;
import com.englishtown.vertx.zookeeper.MatchBehavior;
import com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper;
import org.apache.curator.framework.api.CuratorWatcher;

import javax.inject.Inject;
import java.util.List;

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
        return getConfigElement(path, MatchBehavior.FIRST);
    }

    @Override
    public Promise<ConfigElement> getConfigElement(String path, MatchBehavior matchBehavior) {
        return getConfigElement(path, null, matchBehavior);
    }

    @Override
    public Promise<ConfigElement> getConfigElement(String path, CuratorWatcher watcher) {
        return getConfigElement(path, watcher, MatchBehavior.FIRST);
    }

    @Override
    public Promise<ConfigElement> getConfigElement(String path, CuratorWatcher watcher, MatchBehavior matchBehavior) {
        Deferred<ConfigElement> d = when.defer();

        configuratorHelper.getConfigElement(path, watcher, matchBehavior, result -> {
            if (result.succeeded()) {
                d.resolve(result.result());
            } else {
                d.reject(result.cause());
            }
        });

        return d.getPromise();
    }

    @Override
    public List<String> getPathPrefixes() {
        return configuratorHelper.getPathPrefixes();
    }
}
