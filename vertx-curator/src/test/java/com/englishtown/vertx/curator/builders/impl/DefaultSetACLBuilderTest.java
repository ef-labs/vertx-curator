package com.englishtown.vertx.curator.builders.impl;

import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorOperation;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultSetACLBuilderTest {

    @Mock
    private CuratorClient client;
    @Mock
    private CuratorFramework framework;
    @Mock(extraInterfaces = ErrorListenerPathable.class)
    private SetACLBuilder builder;
    @Mock
    private List<ACL> aclList;

    private String path = "/test/path";
    private int version = 1;

    @Mock
    Handler<AsyncResult<CuratorEvent>> handler;

    @Mock
    BackgroundPathable<Stat> pathable;

    @Test
    public void testBuild() throws Exception {

        DefaultSetACLBuilder target = new DefaultSetACLBuilder();

        CuratorOperation operation = target.forPath(path)
                .withACL(aclList)
                .withVersion(version)
                .build();

        when(client.getCuratorFramework()).thenReturn(framework);
        when(framework.setACL()).thenReturn(builder);
        when(builder.withACL(aclList)).thenReturn(pathable);
        ErrorListenerPathable<Stat> elp = (ErrorListenerPathable<Stat>) builder;
        when(pathable.inBackground(any(BackgroundCallback.class))).thenReturn(elp);

        operation.execute(client, handler);

        verify(builder).withACL(aclList);
        verify(pathable).inBackground(any(BackgroundCallback.class));
        verify(elp).forPath(path);
    }
}
