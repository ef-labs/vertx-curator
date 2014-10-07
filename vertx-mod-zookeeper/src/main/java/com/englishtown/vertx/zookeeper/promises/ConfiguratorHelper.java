package com.englishtown.vertx.zookeeper.promises;

import com.englishtown.promises.Promise;

/**
 */
public interface ConfiguratorHelper {
    public Promise<byte[]> getConfigElement(String path);
}
