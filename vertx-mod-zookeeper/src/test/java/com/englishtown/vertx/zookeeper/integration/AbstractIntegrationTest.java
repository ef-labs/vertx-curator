package com.englishtown.vertx.zookeeper.integration;

import com.englishtown.promises.When;
import com.englishtown.vertx.promises.hk2.HK2WhenBinder;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.hk2.HK2WhenZooKeeperBinder;
import com.englishtown.vertx.zookeeper.promises.WhenConfiguratorHelper;
import com.englishtown.vertx.zookeeper.promises.WhenZooKeeperClient;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
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

    protected ServiceLocator locator;
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

        locator = ServiceLocatorFactory.getInstance().create(null);

        ServiceLocatorUtilities.bind(locator, new HK2WhenZooKeeperBinder(), new HK2WhenBinder(), new AbstractBinder() {
            @Override
            protected void configure() {
                bind(vertx).to(Vertx.class);
                bind(container).to(Container.class);
            }
        });

        when = locator.getService(When.class);
        zookeeperClient = locator.getService(ZooKeeperClient.class);
        whenZookeeperClient = locator.getService(WhenZooKeeperClient.class);
        operationBuilders = locator.getService(ZooKeeperOperationBuilders.class);
        configuratorHelper = locator.getService(WhenConfiguratorHelper.class);

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
