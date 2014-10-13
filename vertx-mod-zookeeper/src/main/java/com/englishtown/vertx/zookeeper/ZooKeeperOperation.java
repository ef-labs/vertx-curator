package com.englishtown.vertx.zookeeper;

import org.apache.curator.framework.api.CuratorEvent;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

/**
 * An executable zookeeper operation
 */
public interface ZooKeeperOperation {

    /**
     * Execute the operation with the provided client and callback handler
     *
     * @param client  zookeeper client
     * @param handler callback handler
     * @throws Exception
     */
    void execute(ZooKeeperClient client, Handler<AsyncResult<CuratorEvent>> handler) throws Exception;

}
