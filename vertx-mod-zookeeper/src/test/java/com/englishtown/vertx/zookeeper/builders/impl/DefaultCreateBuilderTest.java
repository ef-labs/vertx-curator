package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultCreateBuilderTest {

    @Mock
    ZooKeeperClient client;

    @Mock
    CuratorFramework framework;

    @Mock
    CreateBuilder builder;

    byte[] data = new byte[0];

    String path = "/test/path";

    @Mock
    List<ACL> aclList;

    @Mock
    Handler<AsyncResult<CuratorEvent>> handler;

    @Test
    public void testBuild_With_Data() throws Exception{

        DefaultCreateBuilder target = new DefaultCreateBuilder();

        ZooKeeperOperation operation = target.withProtection()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .data(data)
                .forPath(path)
                .withACL(aclList)
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.create()).thenReturn(builder);

        operation.execute(client, handler);

        verify(builder).inBackground(any(BackgroundCallback.class));
        verify(builder).creatingParentsIfNeeded();
        verify(builder).withMode(CreateMode.EPHEMERAL);
        verify(builder).withACL(aclList);
        verify(builder).withProtection();
        verify(builder).forPath(path, data);
    }

    @Test
    public void testBuild_Without_Data() throws Exception{

        DefaultCreateBuilder target = new DefaultCreateBuilder();

        ZooKeeperOperation operation = target.withProtection()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path)
                .withACL(aclList)
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.create()).thenReturn(builder);

        operation.execute(client, handler);

        verify(builder).inBackground(any(BackgroundCallback.class));
        verify(builder).creatingParentsIfNeeded();
        verify(builder).withMode(CreateMode.EPHEMERAL);
        verify(builder).withACL(aclList);
        verify(builder).withProtection();
        verify(builder).forPath(path);
    }
}
