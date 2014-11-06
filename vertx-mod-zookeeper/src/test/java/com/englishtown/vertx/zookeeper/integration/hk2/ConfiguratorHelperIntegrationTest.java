package com.englishtown.vertx.zookeeper.integration.hk2;

import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.junit.Test;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.VertxAssert;

import static org.apache.zookeeper.Watcher.Event.EventType.NodeDataChanged;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 */
public class ConfiguratorHelperIntegrationTest extends AbstractIntegrationTest {

    private CuratorFramework curatorFramework;

    @Override
    protected JsonObject createZooKeeperConfig() {
        JsonObject json = super.createZooKeeperConfig();

        return json.putArray("path_prefixes", new JsonArray()
                .addString("/test/env/dev/application")
                .addString("/test/env/dev")
                .addString("/test/global"));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        curatorFramework = zookeeperClient.getCuratorFramework();

        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/global/cassandra/seeds", "10.0.0.1,10.0.0.2".getBytes());
        tearDownPaths.add("/test/global/cassandra/seeds");
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/env/dev/cassandra/seeds", "192.168.0.1,192.168.0.2".getBytes());
        tearDownPaths.add("/test/env/dev/cassandra/seeds");
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/env/dev/application/cassandra/seeds", "0.0.0.0".getBytes());
        tearDownPaths.add("/test/env/dev/application/cassandra/seeds");
    }

    @Test
    public void testGetConfigElement() throws Exception {

        // First time we try and get the seeds variable, it should return 0.0.0.0
        configuratorHelper.getConfigElement("/cassandra/seeds")
                .then(element -> {
                    VertxAssert.assertNotNull(element);
                    VertxAssert.assertEquals("0.0.0.0", element.asString());

                    // Assuming that is true then wipe out the application one and try again
                    try {
                        curatorFramework.delete().forPath("/test/env/dev/application/cassandra/seeds");
                        return configuratorHelper.getConfigElement("/cassandra/seeds");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .then(element -> {
                    VertxAssert.assertNotNull(element);
                    VertxAssert.assertEquals("192.168.0.1,192.168.0.2", element.asString());

                    // Now wipe out the environment znode and go again.
                    try {
                        curatorFramework.delete().forPath("/test/env/dev/cassandra/seeds");
                        return configuratorHelper.getConfigElement("/cassandra/seeds");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .then(element -> {
                    VertxAssert.assertNotNull(element);
                    VertxAssert.assertEquals("10.0.0.1,10.0.0.2", element.asString());

                    VertxAssert.testComplete();

                    return null;
                })
                .otherwise(this::onRejected);
    }

    @Test
    public void testGetConfigElement_Watcher() throws Exception {

        String updatedData = "127.0.0.1";

        CuratorWatcher watcher = we -> {
            VertxAssert.assertEquals(NodeDataChanged, we.getType());
            VertxAssert.testComplete();
        };

        // First time we try and get the seeds variable, it should return 0.0.0.0
        configuratorHelper.getConfigElement("/cassandra/seeds", watcher)
                .then(element -> {
                    assertNotNull(element);
                    assertEquals("0.0.0.0", element.asString());

                    ZooKeeperOperation setData = operationBuilders.setData()
                            .data(updatedData.getBytes())
                            .forPath(element.getCuratorEvent().getPath())
                            .build();

                    return whenZookeeperClient.execute(setData);
                })
                .then(ce -> {
                    assertEquals(0, ce.getResultCode());
                    return null;
                })
                .otherwise(this::onRejected);
    }

}
