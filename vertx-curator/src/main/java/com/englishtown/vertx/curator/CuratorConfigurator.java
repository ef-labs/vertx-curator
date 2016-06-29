package com.englishtown.vertx.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

/**
 * Curator configuration
 */
public interface CuratorConfigurator {

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
     * Optional path suffixes used when getting data with the {@link com.englishtown.vertx.curator.ConfiguratorHelper}
     *
     * @return the list of path suffixes
     */
    List<String> getPathSuffixes();

    /**
     * Callback for when the configurator is ready
     *
     * @param callback the callback
     */
    void onReady(Handler<AsyncResult<Void>> callback);

    interface AuthPolicy {

        String geScheme();

        String getAuth();

    }
}
