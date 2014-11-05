package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.DeleteBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultDeleteBuilderTest {

    @Mock
    ZooKeeperClient client;

    @Mock
    CuratorFramework framework;

    @Mock
    DeleteBuilder builder;

    int version = 1;

    String path = "/test/path";

    @Mock
    Handler<AsyncResult<CuratorEvent>> handler;

    @Test
    public void testBuild() throws Exception{

        DefaultDeleteBuilder target = new DefaultDeleteBuilder();

        ZooKeeperOperation operation = target.withVersion(version)
                .deletingChildrenIfNeeded()
                .forPath(path)
                .guaranteed()
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.delete()).thenReturn(builder);
        when(builder.inBackground(any(BackgroundCallback.class))).thenReturn(builder);

        operation.execute(client, handler);

        verify(builder).inBackground(any(BackgroundCallback.class));
        verify(builder).deletingChildrenIfNeeded();
        verify(builder).guaranteed();
        verify(builder).withVersion(version);
        verify(builder).forPath(path);
    }
}
