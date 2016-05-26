package com.englishtown.vertx.curator.impl;

import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorConfigurator;
import com.englishtown.vertx.curator.CuratorOperation;
import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import static org.apache.curator.framework.CuratorFrameworkFactory.builder;

/**
 */
public class DefaultCuratorClient implements CuratorClient {

    private final Vertx vertx;
    private final List<Handler<AsyncResult<Void>>> onReadyCallbacks = new ArrayList<>();

    private CuratorFramework framework;
    private AsyncResult<Void> initResult;

    private static final Logger logger = LoggerFactory.getLogger(DefaultCuratorClient.class);

    @Inject
    public DefaultCuratorClient(Vertx vertx, CuratorConfigurator configurator) {
        this.vertx = vertx;

        configurator.onReady(result -> {
            if (result.failed()) {
                initResult = result;
            } else {
                try {
                    initResult = init(configurator);
                } catch (Throwable t) {
                    initResult = Future.failedFuture(t);
                }
            }
            runOnReadyCallbacks(initResult);
        });

    }

    private DefaultCuratorClient(Vertx vertx, CuratorFramework framework, AsyncResult<Void> initResult) {
        this.vertx = vertx;
        this.framework = framework;
        this.initResult = initResult;
    }

    private AsyncResult<Void> init(CuratorConfigurator configurator) throws Exception {

        Builder builder = builder().retryPolicy(configurator.getRetryPolicy());

        CuratorConfigurator.AuthPolicy auth = configurator.getAuthPolicy();
        if (auth != null) {
            builder.authorization(auth.geScheme(), auth.getAuth().getBytes());
        }

        EnsembleProvider ensembleProvider = configurator.getEnsembleProvider();
        if (ensembleProvider != null) {
            builder.ensembleProvider(ensembleProvider);
        } else {
            builder.connectString(configurator.getConnectionString());
        }

        framework = builder.build();
        framework.start();
        framework.getZookeeperClient().getZooKeeper();

        logger.info("Curator client has successfully started");
        return Future.succeededFuture();
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
    public void execute(CuratorOperation operation, Handler<AsyncResult<CuratorEvent>> handler) {
        try {
            operation.execute(this, wrapHandler(handler));
        } catch (Exception e) {
            handler.handle(Future.failedFuture(e));
        }
    }

    @Override
    public CuratorClient usingNamespace(String namespace) {
        if (!initialized() || framework == null) {
            throw new IllegalStateException("Cannot call usingNamespace() until after onReady() successfully completes");
        }
        return new DefaultCuratorClient(vertx, framework.usingNamespace(namespace), initResult);
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

    @Override
    public CuratorWatcher wrapWatcher(CuratorWatcher watcher) {
        Context context = vertx.getOrCreateContext();

        return event -> {
            context.runOnContext(aVoid -> {
                try {
                    watcher.process(event);
                } catch (Exception e) {
                    logger.warn("CuratorWatcher threw an exception", e);
                }
            });
        };
    }

    private Handler<AsyncResult<CuratorEvent>> wrapHandler(Handler<AsyncResult<CuratorEvent>> toWrap) {
        Context context = vertx.getOrCreateContext();

        if (context == null) {
            logger.warn("Current vertx context is null, are you running on the correct thread?");
            return toWrap;
        }

        return (result) -> {
            context.runOnContext(aVoid -> {
                toWrap.handle(result);
            });
        };
    }

    @Override
    public void close() {
        getCuratorFramework().close();
    }
}
