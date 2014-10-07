package com.englishtown.vertx.zookeeper.integration;

import com.englishtown.promises.When;
import com.englishtown.promises.WhenFactory;
import com.englishtown.vertx.zookeeper.builders.GetDataBuilder;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.builders.impl.DefaultGetDataBuilder;
import com.englishtown.vertx.zookeeper.builders.impl.DefaultZooKeeperOperationBuilders;
import com.englishtown.vertx.zookeeper.promises.impl.DefaultConfiguratorHelper;
import com.englishtown.vertx.zookeeper.impl.DefaultZooKeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import javax.inject.Provider;

/**
 */
public class BasicIntegrationTest extends TestVerticle {

    private When when;
    private DefaultZooKeeperClient curatorClient;
    private DefaultConfiguratorHelper configuratorClient;
    private CuratorFramework curatorFramework;

    @Override
    public void start() {
        when = WhenFactory.createSync();

        // Builders
        Provider<GetDataBuilder> getDataBuilderProvider = DefaultGetDataBuilder::new;
        ZooKeeperOperationBuilders zooKeeperOperationBuilders = new DefaultZooKeeperOperationBuilders(getDataBuilderProvider);

        curatorClient = new DefaultZooKeeperClient(vertx);
        configuratorClient = new DefaultConfiguratorHelper(container, when, curatorClient, zooKeeperOperationBuilders);

        curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(100, 3));
        curatorFramework.start();

        initialize();
        startTests();
    }

    @Override
    public void stop() {
//        curatorFramework.close();
    }

    @Test
    public void testReadingTheApplicationConfigValue() throws Exception {
        setupTestData();

        // First time we try and get the seeds variable, it should return 0.0.0.0
        configuratorClient.getConfigElement("/cassandra/seeds").then(
                element -> {
                    VertxAssert.assertNotNull(element);
                    VertxAssert.assertEquals("0.0.0.0", new String(element));

                    // Assuming that is true then wipe out the application one and try again
                    try {
                        curatorFramework.delete().forPath("/test/env/dev/application/cassandra/seeds");
                        return configuratorClient.getConfigElement("/cassandra/seeds");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).then(
                element -> {
                    VertxAssert.assertNotNull(element);
                    VertxAssert.assertEquals("192.168.0.1,192.168.0.2", new String(element));

                    // Now wipe out the environment znode and go again.
                    try {
                        curatorFramework.delete().forPath("/test/env/dev/cassandra/seeds");
                        return configuratorClient.getConfigElement("/cassandra/seeds");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).then(
                element -> {
                    VertxAssert.assertNotNull(element);
                    VertxAssert.assertEquals("10.0.0.1,10.0.0.2", new String(element));

                    VertxAssert.testComplete();

                    return null;
                }
        ).otherwise(
                t -> {
                    VertxAssert.handleThrowable(t);
                    VertxAssert.testComplete();

                    return null;
                }
        );
    }

    public void setupTestData() throws Exception {
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/global/cassandra/seeds", "10.0.0.1,10.0.0.2".getBytes());
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/env/dev/cassandra/seeds", "192.168.0.1,192.168.0.2".getBytes());
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/env/dev/application/cassandra/seeds", "0.0.0.0".getBytes());
    }
}
