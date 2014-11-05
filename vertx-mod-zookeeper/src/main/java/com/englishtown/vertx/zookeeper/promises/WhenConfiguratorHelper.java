package com.englishtown.vertx.zookeeper.promises;

import com.englishtown.promises.Promise;
import com.englishtown.vertx.zookeeper.ConfigElement;
import com.englishtown.vertx.zookeeper.MatchBehavior;
import org.apache.curator.framework.api.CuratorWatcher;

import java.util.List;

/**
 * Promises version of {@link com.englishtown.vertx.zookeeper.ConfiguratorHelper}
 */
public interface WhenConfiguratorHelper {

    Promise<ConfigElement> getConfigElement(String path);

    Promise<ConfigElement> getConfigElement(String path, MatchBehavior matchBehavior);

    Promise<ConfigElement> getConfigElement(String path, CuratorWatcher watcher);

    Promise<ConfigElement> getConfigElement(String path, CuratorWatcher watcher, MatchBehavior matchBehavior);

    List<String> getPathPrefixes();

}
