package com.englishtown.vertx.zookeeper;

import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import org.apache.curator.framework.api.CuratorEvent;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

/**
 */
public interface ZooKeeperOperation {
    void run(ZooKeeperClient client, Handler<AsyncResult<CuratorEvent>> handler) throws Exception;
}
