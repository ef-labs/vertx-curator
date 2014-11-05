package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
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
public class DefaultSetACLBuilderTest {

    @Mock
    ZooKeeperClient client;

    @Mock
    CuratorFramework framework;

    @Mock
    SetACLBuilder builder;

    String path = "/test/path";

    @Mock
    List<ACL> aclList;

    int version = 1;

    @Mock
    Handler<AsyncResult<CuratorEvent>> handler;

    @Mock
    BackgroundPathable<Stat> pathable;

    @Test
    public void testBuild() throws Exception{

        DefaultSetACLBuilder target = new DefaultSetACLBuilder();

        ZooKeeperOperation operation = target.forPath(path)
                .withACL(aclList)
                .withVersion(version)
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.setACL()).thenReturn(builder);
        when(builder.withACL(aclList)).thenReturn(pathable);
        when(pathable.inBackground(any(BackgroundCallback.class))).thenReturn(pathable);

        operation.execute(client, handler);

        verify(builder).withACL(aclList);
        verify(pathable).inBackground(any(BackgroundCallback.class));
        verify(pathable).forPath(path);
    }
}
