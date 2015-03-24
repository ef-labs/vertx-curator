package com.englishtown.vertx.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 */
public interface ZooKeeperClient {

    CuratorFramework getCuratorFramework();

    void execute(ZooKeeperOperation operation, Handler<AsyncResult<CuratorEvent>> handler);

    ZooKeeperClient usingNamespace(String namespace);

    boolean initialized();

    void onReady(Handler<AsyncResult<Void>> callback);

    CuratorWatcher wrapWatcher(CuratorWatcher watcher);

    void close();

}
