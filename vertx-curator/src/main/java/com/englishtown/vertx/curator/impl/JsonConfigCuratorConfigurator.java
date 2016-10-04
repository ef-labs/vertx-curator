package com.englishtown.vertx.curator.impl;

import com.englishtown.vertx.curator.CuratorConfigurator;
import com.google.common.base.Strings;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.exhibitor.DefaultExhibitorRestClient;
import org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider;
import org.apache.curator.ensemble.exhibitor.Exhibitors;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.retry.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * json.config implementation of {@link CuratorConfigurator}
 */
public class JsonConfigCuratorConfigurator implements CuratorConfigurator {

    protected String connectionString;
    protected RetryPolicy retryPolicy;
    protected AuthPolicy authPolicy;
    protected List<String> pathSuffixes;
    protected EnsembleProvider ensembleProvider;

    public final static String FIELD_CURATOR = "curator";
    public final static String FIELD_ZOOKEEPER = "zookeeper";
    public final static String FIELD_CONNECTION_STRING = "connection_string";
    public final static String FIELD_RETRY = "retry";
    public final static String FIELD_AUTH = "auth";
    public final static String FIELD_PATH_SUFFIXES = "path_suffixes";
    public final static String FIELD_ENSEMBLE = "ensemble";

    public final static String DEFAULT_CONNECTION_STRING = "127.0.0.1:2181";

    @Inject
    public JsonConfigCuratorConfigurator(Vertx vertx) {
        this(getConfig(vertx));
    }

    public JsonConfigCuratorConfigurator(JsonObject config) {
        init(config);
    }

    protected void init(JsonObject config) {
        initConnectionString(config.getString(FIELD_CONNECTION_STRING));
        initRetryPolicy(config.getJsonObject(FIELD_RETRY));
        initAuthPolicy(config.getJsonObject(FIELD_AUTH));
        initPathSuffixes(config.getJsonArray(FIELD_PATH_SUFFIXES));
        initEnsembleProvider(config.getJsonObject(FIELD_ENSEMBLE));
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

            if (matchesClass(RetryNTimes.class, type)) {
                retryPolicy = new RetryNTimes(retryConfig.getInteger("n", 10), retryConfig.getInteger("sleep", 500));
            } else if (matchesClass(RetryOneTime.class, type)) {
                retryPolicy = new RetryOneTime(retryConfig.getInteger("sleep", 500));
            } else if (matchesClass(RetryUntilElapsed.class, type)) {
                retryPolicy = new RetryUntilElapsed(retryConfig.getInteger("max_elapsed", 5000), retryConfig.getInteger("sleep", 500));
            } else if (matchesClass(ExponentialBackoffRetry.class, type)) {
                retryPolicy = new ExponentialBackoffRetry(retryConfig.getInteger("base_sleep", 500), retryConfig.getInteger("max_retries", 10));
            } else if (matchesClass(BoundedExponentialBackoffRetry.class, type)) {
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

    protected void initEnsembleProvider(JsonObject ensembleConfig) {

        if (ensembleConfig == null) {
            return;
        }

        String name = ensembleConfig.getString("name");

        if (Strings.isNullOrEmpty(name)) {
            return;
        }

        // ExhibitorEnsembleProvider
        if (matchesClass(ExhibitorEnsembleProvider.class, name)) {

            List<String> hosts = convertToList(ensembleConfig.getJsonArray("hosts"), "Hosts must be of type string: ");
            if (hosts == null || hosts.size() == 0) {
                throw new IllegalArgumentException("Exhibitor ensemble provider must have hosts");
            }

            int restPort = ensembleConfig.getInteger("rest_port", 8080);
            String restUriPath = ensembleConfig.getString("rest_uri_path", "/exhibitor/v1/cluster/list");
            int pollingMs = ensembleConfig.getInteger("polling_ms", 5000);
            String backupConnectionString = ensembleConfig.getString("backup_connection_string", getConnectionString());

            Exhibitors exhibitors = new Exhibitors(hosts, restPort, () -> backupConnectionString);
            ensembleProvider = new ExhibitorEnsembleProvider(exhibitors, new DefaultExhibitorRestClient(), restUriPath, pollingMs, getRetryPolicy());

        // FixedEnsembleProvider
        } else if (matchesClass(FixedEnsembleProvider.class, name)) {

            if (Strings.isNullOrEmpty(connectionString)) {
                throw new IllegalArgumentException("Fixed ensemble requires a valid connection string");
            }

            ensembleProvider = new FixedEnsembleProvider(connectionString);
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
     * Optional path suffixes used when getting data with the {@link com.englishtown.vertx.curator.ConfiguratorHelper}
     *
     * @return the list of path suffixes
     */
    @Override
    public List<String> getPathSuffixes() {
        return pathSuffixes;
    }

    @Override
    public void onReady(Handler<AsyncResult<Void>> callback) {
        callback.handle(Future.succeededFuture(null));
    }

    private static JsonObject getConfig(Vertx vertx) {
        JsonObject config = vertx.getOrCreateContext().config();

        if (config.containsKey(FIELD_CURATOR)) {
            return config.getJsonObject(FIELD_CURATOR, new JsonObject());
        }
        if (config.containsKey(FIELD_ZOOKEEPER)) {
            return config.getJsonObject(FIELD_ZOOKEEPER, new JsonObject());
        }

        return new JsonObject();
    }

    private boolean matchesClass(Class clazz, String name) {
        return clazz.getName().equalsIgnoreCase(name) || clazz.getSimpleName().equalsIgnoreCase(name);
    }

}
