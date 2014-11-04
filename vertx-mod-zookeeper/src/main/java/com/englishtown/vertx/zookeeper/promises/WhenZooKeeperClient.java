package com.englishtown.vertx.zookeeper.promises;

import com.englishtown.promises.Promise;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;

/**
 */
public interface WhenZooKeeperClient {

    CuratorFramework getCuratorFramework();

    Promise<CuratorEvent> execute(ZooKeeperOperation operation);

    boolean initialized();

    Promise<Void> onReady();
}
