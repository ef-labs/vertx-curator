package com.englishtown.vertx.zookeeper;

import org.apache.curator.framework.api.CuratorWatcher;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

/**
 *
 */
public interface ConfiguratorHelper {

    public void getConfigElement(String path, Handler<AsyncResult<ConfigElement>> callback);

    public void getConfigElement(String path, CuratorWatcher watcher, Handler<AsyncResult<ConfigElement>> callback);

}
