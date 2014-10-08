package com.englishtown.vertx.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

/**
 */
public interface ZooKeeperClient {

    CuratorFramework getCuratorFramework();

    void execute(ZooKeeperOperation operation, Handler<AsyncResult<CuratorEvent>> handler);

    boolean initialized();

    void onReady(Handler<Void> callback);
}
