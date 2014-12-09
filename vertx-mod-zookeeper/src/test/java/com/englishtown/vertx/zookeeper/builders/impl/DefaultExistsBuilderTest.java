package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.ExistsBuilder;
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
public class DefaultExistsBuilderTest {

    @Mock
    ZooKeeperClient client;

    @Mock
    CuratorFramework framework;

    @Mock
    ExistsBuilder builder;

    @Mock
    CuratorWatcher watcher;

    String path = "/test/path";

    @Mock
    Handler<AsyncResult<CuratorEvent>> handler;

    @Test
    public void testBuild() throws Exception{

        DefaultExistsBuilder target = new DefaultExistsBuilder();

        ZooKeeperOperation operation = target.forPath(path)
                .usingWatcher(watcher)
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.checkExists()).thenReturn(builder);
        when(builder.inBackground(any(BackgroundCallback.class))).thenReturn(builder);

        operation.execute(client, handler);

        verify(builder).inBackground(any(BackgroundCallback.class));
        verify(builder).usingWatcher(any(CuratorWatcher.class));
        verify(builder).forPath(path);
    }
}
