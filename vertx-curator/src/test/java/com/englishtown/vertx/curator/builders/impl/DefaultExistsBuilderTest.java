package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultExistsBuilderTest {

    @Mock
    private CuratorClient client;
    @Mock
    private CuratorFramework framework;
    @Mock(extraInterfaces = ErrorListenerPathable.class)
    private ExistsBuilder builder;
    @Mock
    private CuratorWatcher watcher;
    @Mock
    private Handler<AsyncResult<CuratorEvent>> handler;

    private String path = "/test/path";

    @Test
    public void testBuild() throws Exception{

        DefaultExistsBuilder target = new DefaultExistsBuilder();

        CuratorOperation operation = target.forPath(path)
                .usingWatcher(watcher)
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.checkExists()).thenReturn(builder);
        ErrorListenerPathable<Stat> elp = (ErrorListenerPathable<Stat>) builder;
        when(builder.inBackground(any(BackgroundCallback.class))).thenReturn(elp);

        operation.execute(client, handler);

        verify(builder).inBackground(any(BackgroundCallback.class));
        verify(builder).usingWatcher(any(CuratorWatcher.class));
        verify(elp).forPath(path);
    }
}
