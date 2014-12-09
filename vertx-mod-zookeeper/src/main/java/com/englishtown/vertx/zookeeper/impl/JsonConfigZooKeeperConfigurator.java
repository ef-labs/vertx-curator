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
import java.util.ArrayList;
import java.util.List;

/**
 * json.config implementation of {@link com.englishtown.vertx.zookeeper.ZooKeeperConfigurator}
 */
public class JsonConfigZooKeeperConfigurator implements ZooKeeperConfigurator {

    protected String connectionString;
    protected RetryPolicy retryPolicy;
    protected AuthPolicy authPolicy;
    protected List<String> pathSuffixes;
    protected EnsembleProvider ensembleProvider;

    public final static String FIELD_CONNECTION_STRING = "connection_string";
    public final static String FIELD_RETRY = "retry";
    public final static String FIELD_AUTH = "auth";
    public final static String FIELD_PATH_SUFFIXES = "path_suffixes";
    public final static String FIELD_ENSEMBLE = "ensemble";

    public final static String DEFAULT_CONNECTION_STRING = "127.0.0.1:2181";

    @Inject
    public JsonConfigZooKeeperConfigurator(Container container) {
        this(container.config().getObject("zookeeper", new JsonObject()));
    }

    public JsonConfigZooKeeperConfigurator(JsonObject config) {
        init(config);
    }

    protected void init(JsonObject config) {
        initConnectionString(config.getString(FIELD_CONNECTION_STRING));
        initRetryPolicy(config.getObject(FIELD_RETRY));
        initAuthPolicy(config.getObject(FIELD_AUTH));
        initPathSuffixes(config.getArray(FIELD_PATH_SUFFIXES));
        initEnsembleProvider(config.getObject(FIELD_ENSEMBLE));
    }

    protected void initConnectionString(String connectionString) {

        if (connectionString == null || connectionString.isEmpty()) {
            connectionString = DEFAULT_CONNECTION_STRING;
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
                auth = authConfig.getString(FIELD_AUTH);
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

    private List<String> convertToList(JsonArray jsonArray, String errorPrefix) {
        List<String> list = new ArrayList<>();

        if (jsonArray != null) {
            for (Object obj : jsonArray) {
                if (obj instanceof String) {
                    list.add((String) obj);
                } else {
                    throw new IllegalArgumentException(errorPrefix + obj);
                }
            }
        }

        return list;
    }

    protected void initPathSuffixes(JsonArray pathConfig) {
        pathSuffixes = convertToList(pathConfig, "Path suffixes must be of type string: ");
    }

    protected void initEnsembleProvider(JsonObject ensemble) {

        if (ensemble == null) {
            return;
        }

        String name = ensemble.getString("name");

        if (Strings.isNullOrEmpty(name)) {
            return;
        }

        if (ExhibitorEnsembleProvider.class.getName().equalsIgnoreCase(name)) {

            List<String> hosts = convertToList(ensemble.getArray("hosts"), "Hosts must be of type string: ");
            if (hosts == null || hosts.size() == 0) {
                throw new IllegalArgumentException("Exhibitor ensemble provider must have hosts");
            }

            int restPort = ensemble.getInteger("rest_port", 8080);
            String restUriPath = ensemble.getString("rest_uri_path", "/exhibitor/v1/cluster/list");
            int pollingMs = ensemble.getInteger("polling_ms", 5000);
            String backupConnectionString = ensemble.getString("backup_connection_string", getConnectionString());

            Exhibitors exhibitors = new Exhibitors(hosts, restPort, () -> backupConnectionString);
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
     * @return the {@link EnsembleProvider}
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
     * Optional path suffixes used when getting data with the {@link com.englishtown.vertx.zookeeper.ConfiguratorHelper}
     *
     * @return the list of path suffixes
     */
    @Override
    public List<String> getPathSuffixes() {
        return pathSuffixes;
    }

    @Override
    public void onReady(Handler<AsyncResult<Void>> callback) {
        callback.handle(new DefaultFutureResult<>((Void) null));
    }
}
