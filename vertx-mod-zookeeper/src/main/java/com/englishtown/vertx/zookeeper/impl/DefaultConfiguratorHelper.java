package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.*;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import com.google.common.base.Strings;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.KeeperException;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.impl.DefaultFutureResult;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static com.englishtown.vertx.zookeeper.MatchBehavior.FIRST;

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
        getConfigElement(elementPath, FIRST, callback);
    }

    @Override
    public void getConfigElement(String elementPath, MatchBehavior matchBehavior, Handler<AsyncResult<ConfigElement>> callback) {
        getConfigElement(elementPath, null, matchBehavior, callback);
    }

    @Override
    public void getConfigElement(String elementPath, CuratorWatcher watcher, Handler<AsyncResult<ConfigElement>> callback) {
        getConfigElement(elementPath, watcher, FIRST, callback);
    }

    @Override
    public void getConfigElement(String elementPath, CuratorWatcher watcher, MatchBehavior matchBehavior, Handler<AsyncResult<ConfigElement>> callback) {
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
            String prefix = pathPrefixes.get(i);
            String path = (Strings.isNullOrEmpty(prefix)) ? elementPath : ZKPaths.makePath(prefix, elementPath);

            ZooKeeperOperation operation = zooKeeperOperationBuilders.getData()
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

                switch (KeeperException.Code.get(event.getResultCode())) {
                    case OK:
                        if (nodeMatches(event, matchBehavior)) {
                            // We have to get the data again, only with a watcher this time, just to set the watcher on our
                            // chosen znode.
                            ZooKeeperOperation operation = zooKeeperOperationBuilders.getData()
                                    .forPath(event.getPath())
                                    .usingWatcher(watcher)
                                    .build();
                            zooKeeperClient.execute(operation, result2 -> {
                                callback.handle(new DefaultFutureResult<>(new DefaultConfigElement(result2.result())));
                            });

                            return;
                        }

                        break;

                    case NOAUTH:
                        logger.warn("Not authorized to view node " + event.getPath());
                    case NONODE:
                        break;

                    default:
                        logger.error("Error while reading node" + event.getPath() + ". Error was " + KeeperException.Code.get(event.getResultCode()).name());
                        callback.handle(new DefaultFutureResult<>(new Exception("Error while reading node: " + KeeperException.Code.get(event.getResultCode()).name())));
                }
            }

            // We didn't find a matching node, so we just resolve with null to show we didn't find one
            callback.handle(new DefaultFutureResult<>(new DefaultConfigElement(null)));
        });
    }

    private boolean nodeMatches(CuratorEvent event, MatchBehavior matchBehavior) {
        switch (matchBehavior) {
            case FIRST_NOTNULL:
                return event.getData() != null;

            default:
                return true;
        }
    }

    public class CountingCompletionHandler<T> {

        private final Context context;
        private final Vertx vertx;
        private int count;
        private int required;
        private Handler<AsyncResult<T>> doneHandler;
        //private Throwable cause;
        //private boolean failed;

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

//        public synchronized void failed(Throwable t) {
//            if (!failed) {
//                // Fail immediately - but only once
//                if (doneHandler != null) {
//                    callHandler(new DefaultFutureResult<T>(t));
//                } else {
//                    cause = t;
//                }
//                failed = true;
//            }
//        }

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
                //if (cause != null) {
                //    callHandler(new DefaultFutureResult<T>(cause));
                //} else {
                if (count == required) {
                    final DefaultFutureResult<T> res = new DefaultFutureResult<>((T) null);
                    callHandler(res);
                }
                //}
            }
        }
    }

    @Override
    public List<String> getPathPrefixes() {
        return pathPrefixes;
    }
}
