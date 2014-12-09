package com.englishtown.vertx.zookeeper.integration.guice;

import com.englishtown.vertx.zookeeper.impl.JsonConfigZooKeeperConfigurator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.VertxAssert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 */
public class ConfiguratorHelperIntegrationTest extends AbstractIntegrationTest {

    private CuratorFramework curatorFramework;

    @Override
    protected JsonObject createZooKeeperConfig() {
        JsonObject json = super.createZooKeeperConfig();

        return json.putArray(JsonConfigZooKeeperConfigurator.FIELD_PATH_SUFFIXES, new JsonArray()
                .addString(".dev.test_app")
                .addString(".dev")
                .addString(".test_app"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        curatorFramework = zookeeperClient.getCuratorFramework();

        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/cassandra/seeds.dev.test_app", "10.0.0.1,10.0.0.2".getBytes());
        tearDownPaths.add("/cassandra/seeds.dev.test_app");
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/cassandra/seeds.test_app", "192.168.0.1,192.168.0.2".getBytes());
        tearDownPaths.add("/cassandra/seeds.test_app");
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/cassandra/seeds", "0.0.0.0".getBytes());
        tearDownPaths.add("/test/cassandra/seeds");
    }

    @Test
    public void testReadingTheApplicationConfigValue() throws Exception {

        // First time we try and get the seeds variable, it should return 0.0.0.0
        configuratorHelper.getConfigElement("/cassandra/seeds")
                .then(element -> {
                    assertNotNull(element);
                    assertEquals("10.0.0.1,10.0.0.2", element.asString());

                    // Assuming that is true then wipe out the application one and try again
                    try {
                        curatorFramework.delete().forPath("/cassandra/seeds.dev.test_app");
                        return configuratorHelper.getConfigElement("/cassandra/seeds");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .then(element -> {
                    assertNotNull(element);
                    assertEquals("192.168.0.1,192.168.0.2", element.asString());

                    // Now wipe out the environment znode and go again.
                    try {
                        curatorFramework.delete().forPath("/cassandra/seeds.test_app");
                        return configuratorHelper.getConfigElement("/cassandra/seeds");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .then(element -> {
                    assertNotNull(element);
                    assertEquals("0.0.0.0", element.asString());

                    VertxAssert.testComplete();
                    return null;
                })
                .otherwise(t -> {
                    VertxAssert.handleThrowable(t);
                    VertxAssert.fail();

                    return null;
                });
    }

}
