package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.*;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.DefaultFutureResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class DefaultConfiguratorHelper implements ConfiguratorHelper {

    // TODO: Move to EnvVarZooKeeperConfigurator implementation
//    private static final String ZOOKEEPER_PATH_PREFIXES_ENVVAR = "zookeeper_path_prefixes";
//    private static final String PATH_DELIMITER = "\\|";
//    pathPrefixes.addAll(Arrays.asList(basePathsString.split(PATH_DELIMITER)));

    private final ZooKeeperClient zooKeeperClient;
    private final ZooKeeperOperationBuilders zooKeeperOperationBuilders;
    private final Vertx vertx;
    private List<String> pathPrefixes = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(DefaultConfiguratorHelper.class);

    @Inject
    public DefaultConfiguratorHelper(
            ZooKeeperConfigurator configurator,
            ZooKeeperClient zooKeeperClient,
            ZooKeeperOperationBuilders zooKeeperOperationBuilders,
            Vertx vertx) {
        this.zooKeeperClient = zooKeeperClient;
        this.zooKeeperOperationBuilders = zooKeeperOperationBuilders;
        this.vertx = vertx;

        configurator.onReady(result -> init(configurator));
    }

    private void init(ZooKeeperConfigurator configurator) {
        pathPrefixes = configurator.getPathPrefixes();
        if (pathPrefixes == null) {
            pathPrefixes = new ArrayList<>();
        }
        if (pathPrefixes.isEmpty()) {
            pathPrefixes.add("");
        }
    }

    @Override
    public void getConfigElement(String elementPath, Handler<AsyncResult<ConfigElement>> callback) {
        getConfigElement(elementPath, null, callback);
    }

    @Override
    public void getConfigElement(String elementPath, CuratorWatcher watcher, Handler<AsyncResult<ConfigElement>> callback) {

        if (elementPath == null) {
            callback.handle(new DefaultFutureResult<>(new IllegalArgumentException("null elementPath")));
            return;
        }

        // We use our list of base paths, appending the desired path to each one, and go looking for each. Ultimately
        // we will resolve with the first non-null response we get. This means that the order of the base paths is important,
        // as the most desired base path should be the first in the list and the least desired last.

        List<AsyncResult<CuratorEvent>> results = new ArrayList<>();
        CountingCompletionHandler<Void> completionHandler = new CountingCompletionHandler<>(vertx);

        for (int i = 0; i < pathPrefixes.size(); i++) {
            completionHandler.incRequired();
            results.add(null);
            String path = pathPrefixes.get(i) + elementPath;

            ZooKeeperOperation operation = zooKeeperOperationBuilders.getData()
                    .usingWatcher(watcher)
                    .forPath(path)
                    .build();

            int index = i;
            zooKeeperClient.execute(operation, result -> {
                results.set(index, result);
                completionHandler.complete();
            });
        }

        completionHandler.setHandler(aVoid -> {

            for (AsyncResult<CuratorEvent> result : results) {
                if (result.failed()) {
                    callback.handle(new DefaultFutureResult<>(result.cause()));
                    return;
                }

                CuratorEvent event = result.result();
                if (event.getData() != null) {
                    callback.handle(new DefaultFutureResult<>(new DefaultConfigElement(event)));
                    return;
                }
            }

            // We didn't find a value that wasn't null, so resolve with null
            callback.handle(new DefaultFutureResult<>(new DefaultConfigElement(null)));
        });

    }

    @Override
    public void getConfigElementChildren(String elementPath, Handler<AsyncResult<ConfigElement>> callback) {
        getConfigElement(elementPath, null, callback);
    }

    @Override
    public void getConfigElementChildren(String elementPath, CuratorWatcher watcher, Handler<AsyncResult<ConfigElement>> callback) {

        if (elementPath == null) {
            callback.handle(new DefaultFutureResult<>(new IllegalArgumentException("null elementPath")));
            return;
        }

        List<AsyncResult<CuratorEvent>> results = new ArrayList<>();
        CountingCompletionHandler<Void> completionHandler = new CountingCompletionHandler<>(vertx);

        for (int i = 0; i < pathPrefixes.size(); i++) {
            completionHandler.incRequired();
            results.add(null);
            String path = pathPrefixes.get(i) + elementPath;

            ZooKeeperOperation operation = zooKeeperOperationBuilders.getChildren()
                    .usingWatcher(watcher)
                    .forPath(path)
                    .build();

            int index = i;
            zooKeeperClient.execute(operation, result -> {
                results.set(index, result);
                completionHandler.complete();
            });
        }

        completionHandler.setHandler(aVoid -> {

            for (AsyncResult<CuratorEvent> result : results) {
                if (result.failed()) {
                    callback.handle(new DefaultFutureResult<>(result.cause()));
                    return;
                }

                CuratorEvent event = result.result();
                if (event.getChildren() != null) {
                    callback.handle(new DefaultFutureResult<>(new DefaultConfigElement(event)));
                    return;
                }
            }

            // We didn't find a value that wasn't null, so resolve with null
            callback.handle(new DefaultFutureResult<>(new DefaultConfigElement(null)));
        });

    }

    public class CountingCompletionHandler<T> {

        private final Context context;
        private final Vertx vertx;
        private int count;
        private int required;
        private Handler<AsyncResult<T>> doneHandler;
        private Throwable cause;
        private boolean failed;

        public CountingCompletionHandler(Vertx vertx) {
            this(vertx, 0);
        }

        public CountingCompletionHandler(Vertx vertx, int required) {
            this.vertx = vertx;
            this.context = vertx.currentContext();
            this.required = required;
        }

        public synchronized void complete() {
            count++;
            checkDone();
        }

        public synchronized void failed(Throwable t) {
            if (!failed) {
                // Fail immediately - but only once
                if (doneHandler != null) {
                    callHandler(new DefaultFutureResult<T>(t));
                } else {
                    cause = t;
                }
                failed = true;
            }
        }

        public synchronized void incRequired() {
            required++;
        }

        public synchronized void setHandler(Handler<AsyncResult<T>> doneHandler) {
            this.doneHandler = doneHandler;
            checkDone();
        }

        private void callHandler(final AsyncResult<T> result) {
            if (vertx.currentContext() == context) {
                doneHandler.handle(result);
            } else {
                context.runOnContext(aVoid -> doneHandler.handle(result));
            }
        }

        void checkDone() {
            if (doneHandler != null) {
                if (cause != null) {
                    callHandler(new DefaultFutureResult<T>(cause));
                } else {
                    if (count == required) {
                        final DefaultFutureResult<T> res = new DefaultFutureResult<T>((T) null);
                        callHandler(res);
                    }
                }
            }
        }
    }
}
