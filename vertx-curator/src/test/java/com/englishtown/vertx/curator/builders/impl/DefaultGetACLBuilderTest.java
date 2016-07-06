package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorOperation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.*;
import org.apache.zookeeper.data.ACL;
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
public class DefaultGetACLBuilderTest {

    @Mock
    private CuratorClient client;
    @Mock
    private CuratorFramework framework;
    @Mock(extraInterfaces = ErrorListenerPathable.class)
    private GetACLBuilder builder;
    @Mock
    private Handler<AsyncResult<CuratorEvent>> handler;

    private String path = "/test/path";

    @Test
    public void testBuild() throws Exception{

        DefaultGetACLBuilder target = new DefaultGetACLBuilder();

        CuratorOperation operation = target.forPath(path)
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.getACL()).thenReturn(builder);
        ErrorListenerPathable<List<ACL>> elp = (ErrorListenerPathable<List<ACL>>) builder;
        when(builder.inBackground(any(BackgroundCallback.class))).thenReturn(elp);

        operation.execute(client, handler);

        verify(builder).inBackground(any(BackgroundCallback.class));
        verify(elp).forPath(path);
    }
}
