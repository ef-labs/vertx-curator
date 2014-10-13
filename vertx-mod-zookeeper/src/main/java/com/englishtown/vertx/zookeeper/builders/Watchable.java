package com.englishtown.vertx.zookeeper.builders;

import org.apache.curator.framework.api.CuratorWatcher;

/**
 * A watchable operation builder
 */
public interface Watchable<T> {

    /**
     * Adds a {@link org.apache.zookeeper.Watcher} to the request. This Watcher will be notified
     * if the node we're getting is modified in any way.
     *
     * @param watcher
     * @return builder of type T
     */
    public T usingWatcher(CuratorWatcher watcher);

}
