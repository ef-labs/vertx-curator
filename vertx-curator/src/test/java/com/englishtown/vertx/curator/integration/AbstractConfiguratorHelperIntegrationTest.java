package com.englishtown.vertx.curator.integration;

import com.englishtown.vertx.curator.CuratorOperation;
import com.englishtown.vertx.curator.impl.JsonConfigCuratorConfigurator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

import static org.apache.zookeeper.Watcher.Event.EventType.NodeDataChanged;

/**
 */
public abstract class AbstractConfiguratorHelperIntegrationTest extends AbstractIntegrationTest {

    private CuratorFramework curatorFramework;

    @Override
    protected JsonObject createCuratorConfig() {
        JsonObject json = super.createCuratorConfig();

        return json.put(JsonConfigCuratorConfigurator.FIELD_PATH_SUFFIXES, new JsonArray()
                .add(".dev.test_app")
                .add(".dev")
                .add(".test_app"));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        curatorFramework = curatorClient.getCuratorFramework();

        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/cassandra/seeds.dev.test_app", "10.0.0.1,10.0.0.2".getBytes());
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/cassandra/seeds.dev", "192.168.0.1,192.168.0.2".getBytes());
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/cassandra/seeds.test_app", "0.0.0.0".getBytes());
    }

    @Test
    public void testGetConfigElement() throws Exception {

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
                        curatorFramework.delete().forPath("/cassandra/seeds.dev");
                        return configuratorHelper.getConfigElement("/cassandra/seeds");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .then(element -> {
                    assertNotNull(element);
                    assertEquals("0.0.0.0", element.asString());

                    testComplete();
                    return null;
                })
                .otherwise(this::onRejected);

        await();
    }

    @Test
    public void testGetConfigElement_Watcher() throws Exception {

        String updatedData = "127.0.0.1";

        CuratorWatcher watcher = we -> {
            assertEquals(NodeDataChanged, we.getType());
            testComplete();
        };

        // First time we try and get the seeds variable, it should return 0.0.0.0
        configuratorHelper.getConfigElement("/cassandra/seeds", watcher)
                .then(element -> {
                    assertNotNull(element);
                    assertEquals("10.0.0.1,10.0.0.2", element.asString());

                    CuratorOperation setData = operationBuilders.setData()
                            .data(updatedData.getBytes())
                            .forPath(element.getCuratorEvent().getPath())
                            .build();

                    return whenCuratorClient.execute(setData);
                })
                .then(ce -> {
                    assertEquals(0, ce.getResultCode());
                    return null;
                })
                .otherwise(this::onRejected);

        await();
    }

}
