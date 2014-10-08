package com.englishtown.vertx.zookeeper.promises;

import com.englishtown.promises.Promise;
import com.englishtown.vertx.zookeeper.ConfigElement;

/**
 */
public interface ConfiguratorHelper {
    public Promise<ConfigElement> getConfigElement(String path);
}
