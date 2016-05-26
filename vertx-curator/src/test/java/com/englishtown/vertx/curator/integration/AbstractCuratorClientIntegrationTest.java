package com.englishtown.vertx.curator.integration;

import com.englishtown.promises.Promise;
import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorOperation;
import com.englishtown.vertx.curator.builders.CreateBuilder;
import com.englishtown.vertx.curator.builders.CuratorOperationBuilders;
import com.englishtown.vertx.curator.impl.JsonConfigCuratorConfigurator;
import io.vertx.core.json.JsonObject;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Integration tests for {@link CuratorOperationBuilders} and {@link CuratorClient}
 */
public abstract class AbstractCuratorClientIntegrationTest extends AbstractIntegrationTest {

    private List<ACL> acls = new ArrayList<>();

    @Override
    public void setUp() throws Exception {
        super.setUp();

        acls.add(new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.AUTH_IDS));
    }

    @Override
    protected JsonObject createCuratorConfig() {
        JsonObject json = super.createCuratorConfig();

        return json.put(JsonConfigCuratorConfigurator.FIELD_AUTH, new JsonObject()
                .put("scheme", "digest")
                .put("username", "test_user")
                .put("password", "test_user_password"));
    }

    @Test
    public void testFullLifeCycle() throws Exception {

        String path = "/full_lifecycle";

        JsonObject data1 = new JsonObject()
                .put("p1", "full lifecycle")
                .put("p2", 123);

        CuratorOperation create = operationBuilders.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .data(data1.encode().getBytes())
                .withACL(acls)
                .forPath(path)
                .build();

        CuratorWatcher watcher = we -> {
            assertEquals(Watcher.Event.EventType.NodeDeleted, we.getType());
            testComplete();
        };

        whenCuratorClient.execute(create)
                .then(ce -> {
                    assertEquals(CuratorEventType.CREATE, ce.getType());

                    CuratorOperation getData = operationBuilders.getData()
                            .usingWatcher(watcher)
                            .forPath(path)
                            .build();

                    return whenCuratorClient.execute(getData);
                })
                .then(ce -> {
                    assertEquals(CuratorEventType.GET_DATA, ce.getType());
                    String str = new String(ce.getData());
                    assertEquals(data1.encode(), str);

                    CuratorOperation getAcl = operationBuilders.getACL()
                            .forPath(path)
                            .build();

                    return whenCuratorClient.execute(getAcl);
                })
                .then(ce -> {
                    assertEquals(CuratorEventType.GET_ACL, ce.getType());
                    List<ACL> list = ce.getACLList();
                    assertTrue(list.size() >= 1);
                    ACL acl = list.get(0);
                    assertEquals(ZooDefs.Perms.ALL, acl.getPerms());
                    assertEquals("digest", acl.getId().getScheme());
                    assertTrue(acl.getId().getId().startsWith("test_user:"));

                    CuratorOperation delete = operationBuilders.delete()
                            .forPath(path)
                            .build();

                    return whenCuratorClient.execute(delete);
                })
                .otherwise(this::onRejected);

        await();
    }

    @Test
    public void testGetChildren() throws Exception {

        String path = "/children";

        curatorClient.getCuratorFramework().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path + "/child1");
        curatorClient.getCuratorFramework().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path + "/child2");

        CuratorOperation operation = operationBuilders
                .getChildren()
                .forPath(path)
                .build();

        curatorClient.execute(operation, result -> {
            if (result.failed()) {
                result.cause().printStackTrace();
                fail();
                return;
            }

            CuratorEvent event = result.result();
            List<String> children = event.getChildren();
            assertNotNull(children);
            assertEquals(2, children.size());
            testComplete();

        });

        await();
    }

    @Test
    public void testCreate() throws Exception {

        String path = "/create";
        String data = "test data";

        CuratorOperation operation = operationBuilders
                .create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path)
                .data(data.getBytes())
                .withACL(acls)
                .build();

        curatorClient.execute(operation, result1 -> {
            if (result1.failed()) {
                result1.cause().printStackTrace();
                fail();
                return;
            }

            curatorClient.execute(operationBuilders.getData().forPath(path).build(), result2 -> {
                if (result2.failed()) {
                    result2.cause().printStackTrace();
                    fail();
                    return;
                }

                String resultData = new String(result2.result().getData());
                assertEquals(data, resultData);
                testComplete();
            });

        });

        await();
    }

    @Test
    public void testDelete() throws Exception {

        String path = "/delete";
        curatorClient.getCuratorFramework().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path + "/child1");

        CuratorOperation operation = operationBuilders
                .delete()
                .deletingChildrenIfNeeded()
                .forPath(path)
                .build();

        curatorClient.execute(operation, result -> {
            if (result.failed()) {
                result.cause().printStackTrace();
                fail();
                return;
            }

            testComplete();

        });

        await();
    }

    @Test
    public void testACLs() throws Exception {

        String path = "/acls";
        List<Promise<CuratorEvent>> promises = new ArrayList<>();

        String path1 = path + "/acl1";
        String path2 = path + "/acl2";

        String id = DigestAuthenticationProvider.generateDigest("test_user2:password2");
        ACL currentUserAcl = new ACL(ZooDefs.Perms.ALL, ZooDefs.Ids.AUTH_IDS);
        ACL testUser2Acl = new ACL(ZooDefs.Perms.ALL, new Id("digest", id));

        CreateBuilder create = operationBuilders.create()
                .withMode(CreateMode.EPHEMERAL)
                .creatingParentsIfNeeded();

        promises.add(whenCuratorClient.execute(create.withACL(ZooDefs.Ids.CREATOR_ALL_ACL).forPath(path1).build()));
        promises.add(whenCuratorClient.execute(create.withACL(Arrays.asList(testUser2Acl, currentUserAcl)).forPath(path2).build()));

        when.all(promises)
                .then(events -> {
                    assertEquals(2, events.size());

                    promises.clear();
                    promises.add(whenCuratorClient.execute(operationBuilders.getData().forPath(path1).build()));
                    promises.add(whenCuratorClient.execute(operationBuilders.getData().forPath(path2).build()));

                    return when.all(promises);
                })
                .then(events -> {
                    assertEquals(2, events.size());

                    CuratorEvent ce1 = events.get(0);
                    CuratorEvent ce2 = events.get(1);

                    assertNotNull(ce1.getData());
                    assertNotNull(ce2.getData());

                    promises.clear();
                    promises.add(whenCuratorClient.execute(operationBuilders.getACL().forPath(path1).build()));
                    promises.add(whenCuratorClient.execute(operationBuilders.getACL().forPath(path2).build()));

                    return when.all(promises);
                })
                .then(events -> {
                    assertEquals(2, events.size());

                    CuratorEvent ce1 = events.get(0);
                    CuratorEvent ce2 = events.get(1);

                    assertNotNull(ce1.getACLList());
                    assertTrue(ce1.getACLList().size() >= 1);
                    ACL acl1 = ce1.getACLList().get(0);
                    assertEquals("digest", acl1.getId().getScheme());
                    assertTrue(acl1.getId().getId().startsWith("test_user:"));

                    assertNotNull(ce2.getACLList());
                    assertTrue(ce2.getACLList().size() >= 2);
                    ACL acl2 = ce2.getACLList().get(0);
                    assertEquals("digest", acl2.getId().getScheme());
                    assertTrue(acl2.getId().getId().startsWith("test_user2:"));

                    testComplete();
                    return null;
                })
                .otherwise(this::onRejected);

        await();
    }

}
