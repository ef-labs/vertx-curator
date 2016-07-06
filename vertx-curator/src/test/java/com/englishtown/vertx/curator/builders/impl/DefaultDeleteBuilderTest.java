package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorOperation;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultDeleteBuilderTest {

    @Mock
    private CuratorClient client;
    @Mock
    private CuratorFramework framework;
    @Mock(extraInterfaces = ErrorListenerPathable.class)
    private DeleteBuilder builder;
    @Mock
    private Handler<AsyncResult<CuratorEvent>> handler;

    private int version = 1;
    private String path = "/test/path";

    @Test
    public void testBuild() throws Exception {

        DefaultDeleteBuilder target = new DefaultDeleteBuilder();

        CuratorOperation operation = target.withVersion(version)
                .deletingChildrenIfNeeded()
                .forPath(path)
                .guaranteed()
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.delete()).thenReturn(builder);
        ErrorListenerPathable<Void> elp = (ErrorListenerPathable<Void>) builder;
        when(builder.inBackground(any(BackgroundCallback.class))).thenReturn(elp);

        operation.execute(client, handler);

        verify(builder).inBackground(any(BackgroundCallback.class));
        verify(builder).deletingChildrenIfNeeded();
        verify(builder).guaranteed();
        verify(builder).withVersion(version);
        verify(elp).forPath(path);
    }
}
