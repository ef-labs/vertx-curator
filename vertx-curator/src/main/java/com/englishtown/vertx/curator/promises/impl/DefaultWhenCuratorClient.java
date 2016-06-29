package com.englishtown.vertx.curator.promises.impl;

import com.englishtown.promises.Deferred;
import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorOperation;
import com.englishtown.vertx.curator.promises.WhenCuratorClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;

import javax.inject.Inject;

/**
 * Default implementation of {@link WhenCuratorClient}
 */
public class DefaultWhenCuratorClient implements WhenCuratorClient {

    private final CuratorClient client;
    private final When when;

    @Inject
    public DefaultWhenCuratorClient(CuratorClient client, When when) {
        this.client = client;
        this.when = when;
    }

    @Override
    public CuratorFramework getCuratorFramework() {
        return client.getCuratorFramework();
    }

    @Override
    public Promise<CuratorEvent> execute(CuratorOperation operation) {
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
    public WhenCuratorClient usingNamespace(String namespace) {
        return new DefaultWhenCuratorClient(client.usingNamespace(namespace), when);
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
        client.close();
    }
}
