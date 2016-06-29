package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.CuratorOperation;
import com.englishtown.vertx.curator.builders.Pathable;
import com.englishtown.vertx.curator.builders.Watchable;
import com.englishtown.vertx.curator.builders.CuratorOperationBuilder;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.common.PathUtils;

/**
 * Abstract builder for curator operation pathable watchable builders
 */
public abstract class AbstractOperationBuilder<T> implements CuratorOperationBuilder, Pathable<T>, Watchable<T> {

    private String path;
    private CuratorWatcher watcher;

    protected abstract CuratorOperation build(String path, CuratorWatcher watcher);

    @Override
    public CuratorOperation build() {
        PathUtils.validatePath(path);
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
