package com.englishtown.vertx.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

import java.util.List;

/**
 * ZooKeeper configuration
 */
public interface ZooKeeperConfigurator {

    /**
     * Standard zookeeper connection string.  This is used if an {@link org.apache.curator.ensemble.EnsembleProvider} is not specified.
     *
     * @return connection string
     */
    String getConnectionString();

    /**
     * Ensemble provider to use instead of a connection string with a {@link org.apache.curator.ensemble.fixed.FixedEnsembleProvider}
     *
     * @return the {@link EnsembleProvider}
     */
    EnsembleProvider getEnsembleProvider();

    /**
     * The curator retry policy to use
     *
     * @return retry policy
     */
    RetryPolicy getRetryPolicy();

    /**
     * Optional authorization policy (digest, ip, etc.)
     *
     * @return auth policy
     */
    AuthPolicy getAuthPolicy();

    /**
     * Optional path prefixes used when getting data with the {@link com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper}
     *
     * @return the list of path prefixes
     */
    List<String> getPathPrefixes();

    /**
     * Callback for when the configurator is ready
     *
     * @param callback the callback
     */
    void onReady(Handler<AsyncResult<Void>> callback);

    public interface AuthPolicy {

        String geScheme();

        String getAuth();

    }
}
