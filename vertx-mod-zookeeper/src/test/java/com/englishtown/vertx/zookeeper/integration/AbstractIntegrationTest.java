package com.englishtown.vertx.zookeeper.integration;

import com.englishtown.promises.When;
import com.englishtown.vertx.promises.hk2.HK2WhenBinder;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.hk2.HK2ZooKeeperBinder;
import com.englishtown.vertx.zookeeper.promises.ConfiguratorHelper;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.vertx.java.core.Future;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;
import org.vertx.testtools.TestVerticle;

/**
 * Base zookeeper integration tests
 */
public abstract class AbstractIntegrationTest extends TestVerticle {

    protected ServiceLocator locator;
    protected When when;
    protected ZooKeeperClient zookeeperClient;
    protected ConfiguratorHelper configuratorHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Future<Void> startedResult) {
        try {
            setup();
            zookeeperClient.onReady(aVoid -> super.start(startedResult));
        } catch (Throwable t) {
            startedResult.setFailure(t);
        }
    }

    protected void setup() throws Exception {

        // Add required zookeeper config
        container.config().putObject("zookeeper", createZooKeeperConfig());

        locator = ServiceLocatorFactory.getInstance().create(null);

        ServiceLocatorUtilities.bind(locator, new HK2ZooKeeperBinder(), new HK2WhenBinder(), new AbstractBinder() {
            @Override
            protected void configure() {
                bind(vertx).to(Vertx.class);
                bind(container).to(Container.class);
            }
        });

        when = locator.getService(When.class);
        zookeeperClient = locator.getService(ZooKeeperClient.class);
        configuratorHelper = locator.getService(ConfiguratorHelper.class);

    }

    protected JsonObject createZooKeeperConfig() {
        return new JsonObject()
                .putString("connection-string", "127.0.0.1:2181");
    }
}
