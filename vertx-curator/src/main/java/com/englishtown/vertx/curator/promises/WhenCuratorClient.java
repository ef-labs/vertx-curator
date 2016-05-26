package com.englishtown.vertx.curator.promises;

import com.englishtown.promises.Promise;
import com.englishtown.vertx.curator.CuratorOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;

/**
 */
public interface WhenCuratorClient {

    CuratorFramework getCuratorFramework();

    Promise<CuratorEvent> execute(CuratorOperation operation);

    WhenCuratorClient usingNamespace(String namespace);

    boolean initialized();

    Promise<Void> onReady();

    void close();
}
