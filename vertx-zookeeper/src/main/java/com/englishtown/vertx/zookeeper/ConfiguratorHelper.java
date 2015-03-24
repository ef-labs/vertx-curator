package com.englishtown.vertx.zookeeper;

import org.apache.curator.framework.api.CuratorWatcher;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

/**
 *
 */
public interface ConfiguratorHelper {

    void getConfigElement(String path, Handler<AsyncResult<ConfigElement>> callback);

    void getConfigElement(String path, MatchBehavior matchBehavior, Handler<AsyncResult<ConfigElement>> callback);

    void getConfigElement(String path, CuratorWatcher watcher, Handler<AsyncResult<ConfigElement>> callback);

    void getConfigElement(String path, CuratorWatcher watcher, MatchBehavior matchBehavior, Handler<AsyncResult<ConfigElement>> callback);

    ConfiguratorHelper usingNamespace(String namespace);

    List<String> getPathSuffixes();

}
