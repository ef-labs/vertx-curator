package com.englishtown.vertx.zookeeper.integration.guice;

import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.guice.GuiceVertxBinder;
import com.englishtown.vertx.promises.guice.GuiceWhenBinder;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.guice.GuiceWhenZooKeeperBinder;
import com.englishtown.vertx.zookeeper.impl.JsonConfigZooKeeperConfigurator;
import com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.zookeeper.promises.WhenZooKeeperClient;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Base zookeeper integration tests
 */
public abstract class AbstractIntegrationTest extends VertxTestBase {

    protected Injector injector;
    protected When when;
    protected ZooKeeperClient zookeeperClient;
    protected WhenZooKeeperClient whenZookeeperClient;
    protected ZooKeeperOperationBuilders operationBuilders;
    protected WhenConfiguratorHelper configuratorHelper;
    protected List<String> tearDownPaths = new ArrayList<>();


    @Override
    public void setUp() throws Exception {
        super.setUp();

        CountDownLatch latch = new CountDownLatch(1);

        vertx.runOnContext(aVoid -> {

            // Add required zookeeper config
            vertx.getOrCreateContext().config().put("zookeeper", createZooKeeperConfig());

            injector = Guice.createInjector(new GuiceWhenZooKeeperBinder(), new GuiceWhenBinder(), new GuiceVertxBinder(vertx));

            when = injector.getInstance(When.class);
            zookeeperClient = injector.getInstance(ZooKeeperClient.class);
            whenZookeeperClient = injector.getInstance(WhenZooKeeperClient.class);
            operationBuilders = injector.getInstance(ZooKeeperOperationBuilders.class);
            configuratorHelper = injector.getInstance(WhenConfiguratorHelper.class);


            when.when(setUpAsync())
                    .otherwise(this::onRejected)
                    .ensure(latch::countDown);

        });

        latch.await();
    }

    protected Promise<Void> setUpAsync() {
        return whenZookeeperClient.onReady();
    }

    @Override
    public void tearDown() throws Exception {

        for (String path : tearDownPaths) {
            try {
                zookeeperClient.getCuratorFramework().delete().deletingChildrenIfNeeded().forPath(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        zookeeperClient.getCuratorFramework().close();
        super.tearDown();

    }

    protected JsonObject createZooKeeperConfig() {
        return new JsonObject()
                .put(JsonConfigZooKeeperConfigurator.FIELD_CONNECTION_STRING, "127.0.0.1:2181");
    }

    protected <T> Promise<T> onRejected(Throwable t) {
        t.printStackTrace();
        fail();
        return null;
    }
}
