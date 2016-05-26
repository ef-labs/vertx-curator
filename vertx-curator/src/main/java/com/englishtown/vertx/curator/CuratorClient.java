package com.englishtown.vertx.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 */
public interface CuratorClient {

    CuratorFramework getCuratorFramework();

    void execute(CuratorOperation operation, Handler<AsyncResult<CuratorEvent>> handler);

    CuratorClient usingNamespace(String namespace);

    boolean initialized();

    void onReady(Handler<AsyncResult<Void>> callback);

    CuratorWatcher wrapWatcher(CuratorWatcher watcher);

    void close();

}
