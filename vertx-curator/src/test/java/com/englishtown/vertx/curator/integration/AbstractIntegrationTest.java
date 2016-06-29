package com.englishtown.vertx.curator.integration;

import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.builders.CuratorOperationBuilders;
import com.englishtown.vertx.curator.impl.JsonConfigCuratorConfigurator;
import com.englishtown.vertx.curator.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.curator.promises.WhenCuratorClient;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.ZKPaths;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.englishtown.vertx.curator.impl.JsonConfigCuratorConfigurator.FIELD_CURATOR;

/**
 * Base curator integration tests
 */
public abstract class AbstractIntegrationTest extends VertxTestBase {

    private CuratorFramework originCuratorFramework;
    protected String testNamespace;
    protected When when;
    protected CuratorClient curatorClient;
    protected WhenCuratorClient whenCuratorClient;
    protected CuratorOperationBuilders operationBuilders;
    protected WhenConfiguratorHelper configuratorHelper;
    protected TestingServer testingServer;

    public void setUp() throws Exception {
        super.setUp();
        initLocator();

        testingServer = createTestingServer();
        CountDownLatch latch = new CountDownLatch(1);

        vertx.runOnContext(aVoid -> {

            // Add required curator config
            JsonObject config = vertx.getOrCreateContext().config();
            config.put(FIELD_CURATOR, createCuratorConfig());

            when = getService(When.class);
            curatorClient = getService(CuratorClient.class);
            whenCuratorClient = getService(WhenCuratorClient.class);
            operationBuilders = getService(CuratorOperationBuilders.class);
            configuratorHelper = getService(WhenConfiguratorHelper.class);

            whenCuratorClient.onReady()
                    .then(aVoid2 -> {

                        originCuratorFramework = whenCuratorClient.getCuratorFramework();
                        testNamespace = "test/" + UUID.randomUUID();

                        try {
                            curatorClient.getCuratorFramework()
                                    .create()
                                    .creatingParentsIfNeeded()
                                    .forPath(ZKPaths.makePath("/", testNamespace));

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        curatorClient = curatorClient.usingNamespace(testNamespace);
                        whenCuratorClient = whenCuratorClient.usingNamespace(testNamespace);
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
        return whenCuratorClient.onReady();
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

    protected JsonObject createCuratorConfig() {
        return new JsonObject()
                .put(JsonConfigCuratorConfigurator.FIELD_CONNECTION_STRING, testingServer.getConnectString());
    }

    protected <T> Promise<T> onRejected(Throwable t) {
        t.printStackTrace();
        fail();
        return null;
    }
}
