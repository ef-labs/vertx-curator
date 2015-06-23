package com.englishtown.vertx.zookeeper.integration;

import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.impl.JsonConfigZooKeeperConfigurator;
import com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.zookeeper.promises.WhenZooKeeperClient;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Base zookeeper integration tests
 */
public abstract class AbstractIntegrationTest extends VertxTestBase {

    private CuratorFramework originCuratorFramework;
    protected String testNamespace;
    protected When when;
    protected ZooKeeperClient zookeeperClient;
    protected WhenZooKeeperClient whenZookeeperClient;
    protected ZooKeeperOperationBuilders operationBuilders;
    protected WhenConfiguratorHelper configuratorHelper;
    protected TestingServer testingServer;

    public void setUp() throws Exception {
        super.setUp();
        initLocator();

        testingServer = createTestingServer();
        CountDownLatch latch = new CountDownLatch(1);

        vertx.runOnContext(aVoid -> {

            // Add required zookeeper config
            JsonObject config = vertx.getOrCreateContext().config();
            config.put("zookeeper", createZooKeeperConfig());

            when = getService(When.class);
            zookeeperClient = getService(ZooKeeperClient.class);
            whenZookeeperClient = getService(WhenZooKeeperClient.class);
            operationBuilders = getService(ZooKeeperOperationBuilders.class);
            configuratorHelper = getService(WhenConfiguratorHelper.class);

            whenZookeeperClient.onReady()
                    .then(aVoid2 -> {

                        originCuratorFramework = whenZookeeperClient.getCuratorFramework();
                        testNamespace = "test/" + UUID.randomUUID();

                        try {
                            zookeeperClient.getCuratorFramework()
                                    .create()
                                    .creatingParentsIfNeeded()
                                    .forPath(ZKPaths.makePath("/", testNamespace));

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        zookeeperClient = zookeeperClient.usingNamespace(testNamespace);
                        whenZookeeperClient = whenZookeeperClient.usingNamespace(testNamespace);
                        configuratorHelper = configuratorHelper.usingNamespace(testNamespace);

                        return setUpAsync();
                    })
                    .otherwise(this::onRejected)
                    .ensure(latch::countDown);

        });

        latch.await();
    }

    protected abstract void initLocator();

    protected abstract <T> T getService(Class<T> clazz);

    protected Promise<Void> setUpAsync() {
        return whenZookeeperClient.onReady();
    }

    @Override
    public void tearDown() throws Exception {
        originCuratorFramework.close();
        testingServer.close();
        super.tearDown();

    }

    protected TestingServer createTestingServer() throws Exception {
        return new TestingServer();
    }

    protected JsonObject createZooKeeperConfig() {
        return new JsonObject()
                .put(JsonConfigZooKeeperConfigurator.FIELD_CONNECTION_STRING, testingServer.getConnectString());
    }

    protected <T> Promise<T> onRejected(Throwable t) {
        t.printStackTrace();
        fail();
        return null;
    }
}
