package com.englishtown.vertx.zookeeper.integration;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.VertxAssert;

/**
 */
public class BasicIntegrationTest extends AbstractIntegrationTest {

    private CuratorFramework curatorFramework;

//    @Override
//    public void start() {
//        when = WhenFactory.createSync();
//
//        // Builders
//        Provider<GetDataBuilder> getDataBuilderProvider = DefaultGetDataBuilder::new;
//        ZooKeeperOperationBuilders zooKeeperOperationBuilders = new DefaultZooKeeperOperationBuilders(getDataBuilderProvider);
//
//        zookeeperClient = new DefaultZooKeeperClient(vertx);
//        configuratorHelper = new DefaultConfiguratorHelper(container, when, zookeeperClient, zooKeeperOperationBuilders);
//
//        curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(100, 3));
//        curatorFramework.start();
//
//        initialize();
//        startTests();
//    }

    @Override
    protected JsonObject createZooKeeperConfig() {
        JsonObject json = super.createZooKeeperConfig();

        return json.putArray("path-prefixes", new JsonArray()
                .addString("/test/env/dev/application")
                .addString("/test/env/dev")
                .addString("/test/global"));
    }

    @Override
    protected void setup() throws Exception {
        super.setup();
        curatorFramework = zookeeperClient.getCuratorFramework();

        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/global/cassandra/seeds", "10.0.0.1,10.0.0.2".getBytes());
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/env/dev/cassandra/seeds", "192.168.0.1,192.168.0.2".getBytes());
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/env/dev/application/cassandra/seeds", "0.0.0.0".getBytes());
    }

    @Override
    public void stop() {
//        curatorFramework.close();
    }

    @Test
    public void testReadingTheApplicationConfigValue() throws Exception {

        // First time we try and get the seeds variable, it should return 0.0.0.0
        configuratorHelper.getConfigElement("/cassandra/seeds").then(
                element -> {
                    VertxAssert.assertNotNull(element);
                    VertxAssert.assertEquals("0.0.0.0", new String(element));

                    // Assuming that is true then wipe out the application one and try again
                    try {
                        curatorFramework.delete().forPath("/test/env/dev/application/cassandra/seeds");
                        return configuratorHelper.getConfigElement("/cassandra/seeds");
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
                        return configuratorHelper.getConfigElement("/cassandra/seeds");
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
                    VertxAssert.fail();

                    return null;
                }
        );
    }

}
