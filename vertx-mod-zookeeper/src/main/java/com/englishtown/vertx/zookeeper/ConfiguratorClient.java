package com.englishtown.vertx.zookeeper;

import com.englishtown.promises.Promise;

/**
 */
public interface ConfiguratorClient {
    public Promise<byte[]> getConfigElement(String path);
}
