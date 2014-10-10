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
    private AsyncResult<Void> initResult;
    private List<Handler<AsyncResult<Void>>> onReadyCallbacks = new ArrayList<>();

    @Inject
    public DefaultZooKeeperClient(Vertx vertx, ZooKeeperConfigurator configurator) {
        this.vertx = vertx;
        this.configurator = configurator;

        configurator.onReady(result -> {
            if (result.failed()) {
                runOnReadyCallbacks(result);
                return;
            }
            init(configurator);
        });
    }

    private void init(ZooKeeperConfigurator configurator) {

        Builder builder = builder()
                .retryPolicy(configurator.getRetryPolicy())
                .connectString(configurator.getConnectionString());

        ZooKeeperConfigurator.AuthPolicy auth = configurator.getAuthPolicy();
        if (auth != null) {
            builder.authorization(auth.geScheme(), auth.getAuth().getBytes());
        }

        framework = builder.build();
        framework.start();

        runOnReadyCallbacks(new DefaultFutureResult<>((Void) null));

    }

    private void runOnReadyCallbacks(AsyncResult<Void> result) {
        initResult = result;
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
        return initResult != null;
    }

    @Override
    public void onReady(Handler<AsyncResult<Void>> callback) {
        if (initResult != null) {
            callback.handle(initResult);
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
