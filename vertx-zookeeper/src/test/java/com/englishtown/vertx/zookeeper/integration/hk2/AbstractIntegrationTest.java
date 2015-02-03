package com.englishtown.vertx.zookeeper.integration.hk2;

import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.hk2.HK2VertxBinder;
import com.englishtown.vertx.promises.hk2.HK2WhenBinder;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.hk2.HK2WhenZooKeeperBinder;
import com.englishtown.vertx.zookeeper.impl.JsonConfigZooKeeperConfigurator;
import com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.zookeeper.promises.WhenZooKeeperClient;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Base zookeeper integration tests
 */
public abstract class AbstractIntegrationTest extends VertxTestBase {

    protected ServiceLocator locator;
    protected When when;
    protected ZooKeeperClient zookeeperClient;
    protected WhenZooKeeperClient whenZookeeperClient;
    protected ZooKeeperOperationBuilders operationBuilders;
    protected WhenConfiguratorHelper configuratorHelper;
    protected List<String> tearDownPaths = new ArrayList<>();


    public void setUp() throws Exception {
        super.setUp();

        CountDownLatch latch = new CountDownLatch(1);

        vertx.runOnContext(aVoid -> {

            // Add required zookeeper config
            JsonObject config = vertx.getOrCreateContext().config();
            config.put("zookeeper", createZooKeeperConfig());

            locator = ServiceLocatorFactory.getInstance().create(null);

            ServiceLocatorUtilities.bind(locator, new HK2WhenZooKeeperBinder(), new HK2WhenBinder(), new HK2VertxBinder(vertx));

            when = locator.getService(When.class);
            zookeeperClient = locator.getService(ZooKeeperClient.class);
            whenZookeeperClient = locator.getService(WhenZooKeeperClient.class);
            operationBuilders = locator.getService(ZooKeeperOperationBuilders.class);
            configuratorHelper = locator.getService(WhenConfiguratorHelper.class);

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
