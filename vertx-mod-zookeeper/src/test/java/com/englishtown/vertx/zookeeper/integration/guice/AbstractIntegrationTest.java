package com.englishtown.vertx.zookeeper.integration.guice;

import com.englishtown.promises.When;
import com.englishtown.vertx.promises.guice.GuiceWhenBinder;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.guice.GuiceWhenZooKeeperBinder;
import com.englishtown.vertx.zookeeper.hk2.HK2WhenZooKeeperBinder;
import com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.zookeeper.promises.WhenZooKeeperClient;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.vertx.java.core.Future;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import java.util.ArrayList;
import java.util.List;

/**
 * Base zookeeper integration tests
 */
public abstract class AbstractIntegrationTest extends TestVerticle {

    protected Injector injector;
    protected When when;
    protected ZooKeeperClient zookeeperClient;
    protected WhenZooKeeperClient whenZookeeperClient;
    protected ZooKeeperOperationBuilders operationBuilders;
    protected WhenConfiguratorHelper configuratorHelper;
    protected List<String> tearDownPaths = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Future<Void> startedResult) {
        try {
            setUp();
            zookeeperClient.onReady(aVoid -> super.start(startedResult));
        } catch (Throwable t) {
            startedResult.setFailure(t);
        }
    }

    protected void setUp() throws Exception {

        // Add required zookeeper config
        container.config().putObject("zookeeper", createZooKeeperConfig());

        injector = Guice.createInjector(new GuiceWhenZooKeeperBinder(), new GuiceWhenBinder(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(Vertx.class).toInstance(vertx);
                bind(Container.class).toInstance(container);
            }
        });

        when = injector.getInstance(When.class);
        zookeeperClient = injector.getInstance(ZooKeeperClient.class);
        whenZookeeperClient = injector.getInstance(WhenZooKeeperClient.class);
        operationBuilders = injector.getInstance(ZooKeeperOperationBuilders.class);
        configuratorHelper = injector.getInstance(WhenConfiguratorHelper.class);

    }

    @Override
    public void stop() {
        tearDown();
        zookeeperClient.getCuratorFramework().close();
    }

    protected void tearDown() {

        for (String path : tearDownPaths) {
            try {
                zookeeperClient.getCuratorFramework().delete().deletingChildrenIfNeeded().forPath(path);
            } catch (Exception e) {
                VertxAssert.handleThrowable(e);
            }
        }

    }

    protected JsonObject createZooKeeperConfig() {
        return new JsonObject()
                .putString("connection_string", "127.0.0.1:2181");
    }
}
