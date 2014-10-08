package com.englishtown.vertx.zookeeper.promises;

import com.englishtown.promises.Promise;
import com.englishtown.vertx.zookeeper.ConfigElement;

/**
 * Promises version of {@link com.englishtown.vertx.zookeeper.ConfiguratorHelper}
 */
public interface WhenConfiguratorHelper {

    Promise<ConfigElement> getConfigElement(String path);

}
