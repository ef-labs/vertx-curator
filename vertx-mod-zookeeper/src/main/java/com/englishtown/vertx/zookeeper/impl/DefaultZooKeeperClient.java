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
import java.util.ArrayList;
import java.util.List;

import static org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import static org.apache.curator.framework.CuratorFrameworkFactory.builder;

/**
 */
public class DefaultZooKeeperClient implements ZooKeeperClient {

    private CuratorFramework framework;
    private Vertx vertx;
    private final ZooKeeperConfigurator configurator;
    private boolean initialized;
    private List<Handler<AsyncResult<Void>>> onReadyCallbacks = new ArrayList<>();

    @Inject
    public DefaultZooKeeperClient(Vertx vertx, ZooKeeperConfigurator configurator) {
        this.vertx = vertx;
        this.configurator = configurator;
        configurator.onReady(this::init);
    }

    private void init(AsyncResult<Void> result) {

        if (result.failed()) {
            runOnReadyCallbacks(result);
            return;
        }

        Builder builder = builder()
                .retryPolicy(configurator.getRetryPolicy())
                .connectString(configurator.getConnectionString());

        ZooKeeperConfigurator.AuthPolicy auth = configurator.getAuthPolicy();
        if (auth != null) {
            builder.authorization(auth.geScheme(), auth.getAuth().getBytes());
        }

        framework = builder.build();
        framework.start();

        initialized = true;
        runOnReadyCallbacks(result);

    }

    private void runOnReadyCallbacks(AsyncResult<Void> result) {
        onReadyCallbacks.forEach(handler -> handler.handle(result));
        onReadyCallbacks.clear();
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

    @Override
    public boolean initialized() {
        return initialized;
    }

    @Override
    public void onReady(Handler<AsyncResult<Void>> callback) {
        if (initialized()) {
            callback.handle(new DefaultFutureResult<>((Void) null));
        } else {
            onReadyCallbacks.add(callback);
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
