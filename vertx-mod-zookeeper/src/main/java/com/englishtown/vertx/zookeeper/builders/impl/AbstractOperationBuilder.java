package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.Pathable;
import com.englishtown.vertx.zookeeper.builders.Watchable;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilder;
import org.apache.curator.framework.api.CuratorWatcher;

/**
 * Abstract builder for zookeeper operation pathable watchable builders
 */
public abstract class AbstractOperationBuilder<T> implements ZooKeeperOperationBuilder, Pathable<T>, Watchable<T> {

    private String path;
    private CuratorWatcher watcher;

    protected abstract ZooKeeperOperation build(String path, CuratorWatcher watcher);

    @Override
    public ZooKeeperOperation build() {
        return build(path, watcher);
    }

    @Override
    public T forPath(String path) {
        this.path = path;
        return getBuilder();
    }

    @Override
    public T usingWatcher(CuratorWatcher watcher) {
        this.watcher = watcher;
        return getBuilder();
    }

    @SuppressWarnings("unchecked")
    protected T getBuilder() {
        return (T) this;
    }

}
