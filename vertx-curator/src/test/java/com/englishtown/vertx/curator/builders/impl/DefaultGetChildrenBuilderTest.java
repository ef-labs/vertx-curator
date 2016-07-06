package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultGetChildrenBuilderTest {

    @Mock
    private CuratorClient client;
    @Mock
    private CuratorFramework framework;
    @Mock(extraInterfaces = ErrorListenerPathable.class)
    private GetChildrenBuilder builder;
    @Mock
    private CuratorWatcher watcher;
    @Mock
    private Handler<AsyncResult<CuratorEvent>> handler;

    private String path = "/test/path";

    @Test
    public void testBuild() throws Exception{

        DefaultGetChildrenBuilder target = new DefaultGetChildrenBuilder();

        CuratorOperation operation = target.forPath(path)
                .usingWatcher(watcher)
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.getChildren()).thenReturn(builder);
        ErrorListenerPathable<List<String>> elp = (ErrorListenerPathable<List<String>>) builder;
        when(builder.inBackground(any(BackgroundCallback.class))).thenReturn(elp);

        operation.execute(client, handler);

        verify(builder).inBackground(any(BackgroundCallback.class));
        verify(elp).forPath(path);
    }
}
