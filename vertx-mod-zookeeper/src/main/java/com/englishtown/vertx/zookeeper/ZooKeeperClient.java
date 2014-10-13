package com.englishtown.vertx.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

/**
 */
public interface ZooKeeperClient {

    CuratorFramework getCuratorFramework();

    void execute(ZooKeeperOperation operation, Handler<AsyncResult<CuratorEvent>> handler);

    boolean initialized();

    void onReady(Handler<AsyncResult<Void>> callback);

    CuratorWatcher wrapWatcher(CuratorWatcher watcher);

}
