package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.RetryNTimes;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.DefaultFutureResult;

import javax.inject.Inject;

/**
 */
public class DefaultZooKeeperClient implements ZooKeeperClient {

    private CuratorFramework framework;
    private Vertx vertx;

    @Inject
    public DefaultZooKeeperClient(Vertx vertx) {
        this.vertx = vertx;
        init();
    }

    //TODO: Take the IP address from environment variable
    private void init() {
        framework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new RetryNTimes(0, 100));
        framework.start();
    }

    @Override
    public CuratorFramework getCuratorFramework() {
        return framework;
    }

    @Override
    public void execute(ZooKeeperOperation operation, Handler<AsyncResult<CuratorEvent>> handler) {
        try {
            operation.run(this, wrapHandler(handler));
        } catch (Exception e) {
            handler.handle(new DefaultFutureResult<>(e));
        }
    }

    private Handler<AsyncResult<CuratorEvent>> wrapHandler(Handler<AsyncResult<CuratorEvent>> toWrap) {
        Context context = vertx.currentContext();

       return (result) -> {
            context.runOnContext(aVoid -> {
                toWrap.handle(result);
            });
        };
    }
}
