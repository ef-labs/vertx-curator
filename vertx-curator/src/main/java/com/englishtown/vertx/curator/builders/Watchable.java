package com.englishtown.vertx.curator.builders;

import org.apache.curator.framework.api.CuratorWatcher;

/**
 * A watchable operation builder
 */
public interface Watchable<T> {

    /**
     * Adds a {@link CuratorWatcher} to the request. This Watcher will be notified
     * if the node we're getting is modified in any way.
     *
     * @param watcher the watcher
     * @return builder of type T
     */
    T usingWatcher(CuratorWatcher watcher);

}
