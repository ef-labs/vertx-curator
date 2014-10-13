package com.englishtown.vertx.zookeeper.promises;

import com.englishtown.promises.Promise;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

/**
 */
public interface WhenZooKeeperClient {

    CuratorFramework getCuratorFramework();

    Promise<CuratorEvent> execute(ZooKeeperOperation operation);

    boolean initialized();

    Promise<Void> onReady();
}
