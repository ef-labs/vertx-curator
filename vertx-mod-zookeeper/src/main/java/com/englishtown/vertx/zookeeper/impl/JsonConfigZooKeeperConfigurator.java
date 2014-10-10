package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import org.apache.curator.RetryPolicy;
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

    @Inject
    public JsonConfigZooKeeperConfigurator(Container container) {
        this(container.config().getObject("zookeeper", new JsonObject()));
    }

    public JsonConfigZooKeeperConfigurator(JsonObject config) {
        this.config = config;
        init(config);
    }

    protected void init(JsonObject config) {
        connectionString = initConnectionString(config.getString("connection-string"));
        retryPolicy = initRetryPolicy(config.getObject("retry"));
        authPolicy = initAuthPolicy(config.getObject("auth"));
        pathPrefixes = initPathPrefixes(config.getArray("path-prefixes"));
    }

    protected String initConnectionString(String connectString) {

        if (connectString == null || connectString.isEmpty()) {
            connectString = "127.0.0.1:2181";
        }

        return connectString;
    }

    protected RetryPolicy initRetryPolicy(JsonObject retryConfig) {

        if (retryConfig != null) {
            String type = retryConfig.getString("type");

            if (RetryNTimes.class.getName().equalsIgnoreCase(type)) {
                return new RetryNTimes(retryConfig.getInteger("n", 10), retryConfig.getInteger("sleep", 500));
            } else if (RetryOneTime.class.getName().equalsIgnoreCase(type)) {
                return new RetryOneTime(retryConfig.getInteger("sleep", 500));
            } else if (RetryOneTime.class.getName().equalsIgnoreCase(type)) {
                return new RetryOneTime(retryConfig.getInteger("sleep", 500));
            } else if (RetryUntilElapsed.class.getName().equalsIgnoreCase(type)) {
                return new RetryUntilElapsed(retryConfig.getInteger("max-elapsed", 5000), retryConfig.getInteger("sleep", 500));
            } else if (ExponentialBackoffRetry.class.getName().equalsIgnoreCase(type)) {
                return new ExponentialBackoffRetry(retryConfig.getInteger("base-sleep", 500), retryConfig.getInteger("max-retries", 10));
            } else if (BoundedExponentialBackoffRetry.class.getName().equalsIgnoreCase(type)) {
                return new BoundedExponentialBackoffRetry(retryConfig.getInteger("base-sleep", 500), retryConfig.getInteger("max-sleep", 5000), retryConfig.getInteger("max-retries", 10));
            }
        }

        return new RetryNTimes(10, 500);

    }

    protected AuthPolicy initAuthPolicy(JsonObject authConfig) {

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

            return new AuthPolicy() {
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

        return null;
    }

    @SuppressWarnings("unchecked")
    protected List<String> initPathPrefixes(JsonArray pathConfig) {

        if (pathConfig != null) {
            return pathConfig.toList();
        }

        return null;
    }

    @Override
    public String getConnectionString() {
        return connectionString;
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
