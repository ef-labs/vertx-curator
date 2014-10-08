package com.englishtown.vertx.zookeeper;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

/**
 *
 */
public interface ConfiguratorHelper {

    public void getConfigElement(String path, Handler<AsyncResult<ConfigElement>> callback);

}
