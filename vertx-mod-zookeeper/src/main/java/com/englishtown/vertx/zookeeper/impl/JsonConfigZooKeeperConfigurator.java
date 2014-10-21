package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import com.google.common.base.Strings;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.exhibitor.DefaultExhibitorRestClient;
import org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider;
import org.apache.curator.ensemble.exhibitor.Exhibitors;
import org.apache.curator.retry.*;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.impl.DefaultFutureResult;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

import javax.inject.Inject;
import java.util.List;

/**
 * json.config implementation of {@link com.englishtown.vertx.zookeeper.ZooKeeperConfigurator}
 */
public class JsonConfigZooKeeperConfigurator implements ZooKeeperConfigurator {

    private JsonObject config;
    protected String connectionString;
    protected RetryPolicy retryPolicy;
    protected AuthPolicy authPolicy;
    protected List<String> pathPrefixes;
    protected EnsembleProvider ensembleProvider;

    @Inject
    public JsonConfigZooKeeperConfigurator(Container container) {
        this(container.config().getObject("zookeeper", new JsonObject()));
    }

    public JsonConfigZooKeeperConfigurator(JsonObject config) {
        this.config = config;
        init(config);
    }

    protected void init(JsonObject config) {
        initConnectionString(config);
        initRetryPolicy(config.getObject("retry"));
        initAuthPolicy(config.getObject("auth"));
        initPathPrefixes(config.getArray("path_prefixes"));
        initEnsembleProvider(config.getObject("ensemble"));
    }

    protected void initConnectionString(JsonObject config) {

        String connectionString = config.getString("connection_string");

        if (connectionString == null || connectionString.isEmpty()) {
            connectionString = "127.0.0.1:2181";
        }

        this.connectionString = connectionString;
    }

    protected void initRetryPolicy(JsonObject retryConfig) {

        RetryPolicy retryPolicy = null;

        if (retryConfig != null) {
            String type = retryConfig.getString("type");

            if (RetryNTimes.class.getName().equalsIgnoreCase(type)) {
                retryPolicy = new RetryNTimes(retryConfig.getInteger("n", 10), retryConfig.getInteger("sleep", 500));
            } else if (RetryOneTime.class.getName().equalsIgnoreCase(type)) {
                retryPolicy = new RetryOneTime(retryConfig.getInteger("sleep", 500));
            } else if (RetryOneTime.class.getName().equalsIgnoreCase(type)) {
                retryPolicy = new RetryOneTime(retryConfig.getInteger("sleep", 500));
            } else if (RetryUntilElapsed.class.getName().equalsIgnoreCase(type)) {
                retryPolicy = new RetryUntilElapsed(retryConfig.getInteger("max_elapsed", 5000), retryConfig.getInteger("sleep", 500));
            } else if (ExponentialBackoffRetry.class.getName().equalsIgnoreCase(type)) {
                retryPolicy = new ExponentialBackoffRetry(retryConfig.getInteger("base_sleep", 500), retryConfig.getInteger("max_retries", 10));
            } else if (BoundedExponentialBackoffRetry.class.getName().equalsIgnoreCase(type)) {
                retryPolicy = new BoundedExponentialBackoffRetry(retryConfig.getInteger("base_sleep", 500), retryConfig.getInteger("max_sleep", 5000), retryConfig.getInteger("max_retries", 10));
            }
        }

        if (retryPolicy == null) {
            retryPolicy = new RetryNTimes(10, 500);
        }

        this.retryPolicy = retryPolicy;

    }

    protected void initAuthPolicy(JsonObject authConfig) {

        if (authConfig != null) {
            String scheme = authConfig.getString("scheme");
            String auth;
            if ("digest".equalsIgnoreCase(scheme)) {
                String username = authConfig.getString("username");
                String password = authConfig.getString("password");
                auth = username + ":" + password;
            } else {
                auth = authConfig.getString("auth");
            }

            authPolicy = new AuthPolicy() {
                @Override
                public String geScheme() {
                    return scheme;
                }

                @Override
                public String getAuth() {
                    return auth;
                }
            };
        }

    }

    @SuppressWarnings("unchecked")
    protected void initPathPrefixes(JsonArray pathConfig) {

        if (pathConfig != null) {
            pathPrefixes = pathConfig.toList();
        }

    }

    protected void initEnsembleProvider(JsonObject ensemble) {

        if (ensemble == null) {
            return;
        }

        String name = ensemble.getString("name");

        if (Strings.isNullOrEmpty(name)) {
            return;

        } else if (ExhibitorEnsembleProvider.class.getName().equalsIgnoreCase(name)) {

            JsonArray hosts = ensemble.getArray("hosts");
            if (hosts == null) {
                throw new IllegalArgumentException("Exhibitor ensemble provider must have hosts");
            }

            int restPort = ensemble.getInteger("rest_port", 8080);
            String restUriPath = ensemble.getString("rest_uri_path", "/exhibitor/v1/cluster/list");
            int pollingMs = ensemble.getInteger("polling_ms", 5000);
            String backupConnectionString = ensemble.getString("backup_connection_string", getConnectionString());

            Exhibitors exhibitors = new Exhibitors(hosts.toList(), restPort, () -> backupConnectionString);
            ensembleProvider = new ExhibitorEnsembleProvider(exhibitors, new DefaultExhibitorRestClient(), restUriPath, pollingMs, getRetryPolicy());

        } else {
            throw new IllegalArgumentException("EnsembleProvider " + name + " is not supported");

        }

    }

    @Override
    public String getConnectionString() {
        return connectionString;
    }

    /**
     * Ensemble provider to use instead of a connection string with a {@link org.apache.curator.ensemble.fixed.FixedEnsembleProvider}
     *
     * @return
     */
    @Override
    public EnsembleProvider getEnsembleProvider() {
        return ensembleProvider;
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    @Override
    public AuthPolicy getAuthPolicy() {
        return authPolicy;
    }

    /**
     * Optional path prefixes used when getting data with the {@link com.englishtown.vertx.zookeeper.ConfiguratorHelper}
     *
     * @return
     */
    @Override
    public List<String> getPathPrefixes() {
        return pathPrefixes;
    }

    @Override
    public void onReady(Handler<AsyncResult<Void>> callback) {
        callback.handle(new DefaultFutureResult<>((Void) null));
    }
}
