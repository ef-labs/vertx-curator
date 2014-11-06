package com.englishtown.vertx.zookeeper.promises.impl;

import com.englishtown.promises.Deferred;
import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.promises.WhenZooKeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;

import javax.inject.Inject;

/**
 * Default implementation of {@link com.englishtown.vertx.zookeeper.promises.WhenZooKeeperClient}
 */
public class DefaultWhenZooKeeperClient implements WhenZooKeeperClient {

    private final ZooKeeperClient client;
    private final When when;

    @Inject
    public DefaultWhenZooKeeperClient(ZooKeeperClient client, When when) {
        this.client = client;
        this.when = when;
    }

    @Override
    public CuratorFramework getCuratorFramework() {
        return client.getCuratorFramework();
    }

    @Override
    public Promise<CuratorEvent> execute(ZooKeeperOperation operation) {
        Deferred<CuratorEvent> d = when.defer();

        client.execute(operation, result -> {
            if (result.succeeded()) {
                d.resolve(result.result());
            } else {
                d.reject(result.cause());
            }
        });

        return d.getPromise();
    }

    @Override
    public boolean initialized() {
        return client.initialized();
    }

    @Override
    public Promise<Void> onReady() {
        Deferred<Void> d = when.defer();

        client.onReady(result -> {
            if (result.succeeded()) {
                d.resolve((Void) null);
            } else {
                d.reject(result.cause());
            }
        });

        return d.getPromise();
    }

    @Override
    public void close() {
        getCuratorFramework().close();
    }
}
