package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.*;
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
    private RetryPolicy retryPolicy;
    private AuthPolicy authPolicy;
    private List<String> pathPrefixes;

    @Inject
    public JsonConfigZooKeeperConfigurator(Container container) {
        this(container.config().getObject("zookeeper", new JsonObject()));
    }

    public JsonConfigZooKeeperConfigurator(JsonObject config) {
        this.config = config;
        init();
    }

    protected void init() {
        retryPolicy = initRetryPolicy();
        authPolicy = initAuthPolicy();
        pathPrefixes = initPathPrefixes();
    }

    protected RetryPolicy initRetryPolicy() {

        JsonObject json = config.getObject("retry");

        if (json != null) {
            String type = json.getString("type");

            if (RetryNTimes.class.getName().equalsIgnoreCase(type)) {
                return new RetryNTimes(json.getInteger("n", 10), json.getInteger("sleep", 500));
            } else if (RetryOneTime.class.getName().equalsIgnoreCase(type)) {
                return new RetryOneTime(json.getInteger("sleep", 500));
            } else if (RetryOneTime.class.getName().equalsIgnoreCase(type)) {
                return new RetryOneTime(json.getInteger("sleep", 500));
            } else if (RetryUntilElapsed.class.getName().equalsIgnoreCase(type)) {
                return new RetryUntilElapsed(json.getInteger("max-elapsed", 5000), json.getInteger("sleep", 500));
            } else if (ExponentialBackoffRetry.class.getName().equalsIgnoreCase(type)) {
                return new ExponentialBackoffRetry(json.getInteger("base-sleep", 500), json.getInteger("max-retries", 10));
            } else if (BoundedExponentialBackoffRetry.class.getName().equalsIgnoreCase(type)) {
                return new BoundedExponentialBackoffRetry(json.getInteger("base-sleep", 500), json.getInteger("max-sleep", 5000), json.getInteger("max-retries", 10));
            }
        }

        return new RetryNTimes(10, 500);

    }

    protected AuthPolicy initAuthPolicy() {

        JsonObject json = config.getObject("auth");

        if (json != null) {
            String scheme = json.getString("scheme");
            String auth;
            if ("digest".equalsIgnoreCase(scheme)) {
                String username = json.getString("username");
                String password = json.getString("password");
                auth = username + ":" + password;
            } else {
                auth = json.getString("auth");
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
    protected List<String> initPathPrefixes() {

        JsonArray json = config.getArray("path-prefixes");

        if (json != null) {
            return json.toList();
        }

        return null;
    }

    @Override
    public String getConnectionString() {
        return config.getString("connection-string");
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
     * Optional path prefixes used when getting data with the {@link com.englishtown.vertx.zookeeper.promises.ConfiguratorHelper}
     *
     * @return
     */
    @Override
    public List<String> getPathPrefixes() {
        return pathPrefixes;
    }
}
