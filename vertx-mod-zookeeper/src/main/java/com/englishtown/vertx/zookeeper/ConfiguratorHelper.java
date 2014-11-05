package com.englishtown.vertx.zookeeper;

import org.apache.curator.framework.api.CuratorWatcher;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

import java.util.List;

/**
 *
 */
public interface ConfiguratorHelper {

    void getConfigElement(String path, Handler<AsyncResult<ConfigElement>> callback);

    void getConfigElement(String path, CuratorWatcher watcher, Handler<AsyncResult<ConfigElement>> callback);

    List<String> getPathPrefixes();

}
