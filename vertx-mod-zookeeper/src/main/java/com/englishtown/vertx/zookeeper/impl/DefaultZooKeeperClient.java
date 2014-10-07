package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.DefaultFutureResult;

import javax.inject.Inject;

import static org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import static org.apache.curator.framework.CuratorFrameworkFactory.builder;

/**
 */
public class DefaultZooKeeperClient implements ZooKeeperClient {

    private CuratorFramework framework;
    private Vertx vertx;
    private final ZooKeeperConfigurator configurator;

    @Inject
    public DefaultZooKeeperClient(Vertx vertx, ZooKeeperConfigurator configurator) {
        this.vertx = vertx;
        this.configurator = configurator;
        init();
    }

    //TODO: Take the IP address from environment variable
    private void init() {
        Builder builder = builder()
                .retryPolicy(configurator.getRetryPolicy())
                .connectString(configurator.getConnectionString());

        ZooKeeperConfigurator.AuthPolicy auth = configurator.getAuthPolicy();
        if (auth != null) {
            builder.authorization(auth.geScheme(), auth.getAuth().getBytes());
        }

        framework = builder.build();
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
