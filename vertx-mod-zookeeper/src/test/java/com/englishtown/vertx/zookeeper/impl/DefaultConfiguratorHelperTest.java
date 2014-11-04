package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Context;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfiguratorHelperTest {

    String elementPath = "test/path";

    @Mock
    CuratorWatcher watcher;

    @Mock
    ZooKeeperConfigurator configurator;

    @Mock
    ZooKeeperClient zooKeeperClient;

    @Mock
    ZooKeeperOperationBuilders zooKeeperOperationBuilders;

    @Mock
    Vertx vertx;

    @Mock
    Context context1;

    @Mock
    Context context2;

    @Mock
    AsyncResult<Void> asyncResultOnReady;

    @Captor
    ArgumentCaptor<Handler<AsyncResult<Void>>> handlerCaptorOnReady;

    @Mock
    AsyncResult<CuratorEvent> asyncResultCuratorEvent;

    @Captor
    ArgumentCaptor<Handler<AsyncResult<CuratorEvent>>> handlerCuratorEvent;

    @Mock
    CuratorEvent event;

    @Mock
    com.englishtown.vertx.zookeeper.builders.GetDataBuilder getDataBuilder;

    @Mock
    ZooKeeperOperation operation;

    @Mock
    Throwable throwable;

    byte[] data = new byte[1];

    private DefaultConfiguratorHelper getTarget() {
        DefaultConfiguratorHelper target = new DefaultConfiguratorHelper(
                configurator, zooKeeperClient, zooKeeperOperationBuilders, vertx);

        verify(configurator).onReady(handlerCaptorOnReady.capture());
        handlerCaptorOnReady.getValue().handle(asyncResultOnReady);

        return target;
    }

    @Test
    public void testGetConfigElement_NullPath() throws Exception {
        getTarget().getConfigElement(null, event -> {
            assertEquals(IllegalArgumentException.class, event.cause().getClass());
        });
    }

    @Test
    public void testGetConfigElement_RunOnExternalContext() throws Exception {
        when(vertx.currentContext()).thenReturn(context1).thenReturn(context2);
        when(zooKeeperOperationBuilders.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.usingWatcher(watcher)).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(elementPath)).thenReturn(getDataBuilder);
        when(getDataBuilder.build()).thenReturn(operation);

        DefaultConfiguratorHelper target = getTarget();
        target.getConfigElement(elementPath, watcher, event -> {
            assertNull(event.result().asBytes());
        });

        verify(zooKeeperClient).execute(eq(operation), handlerCuratorEvent.capture());
        when(asyncResultCuratorEvent.result()).thenReturn(event);
        handlerCuratorEvent.getValue().handle(asyncResultCuratorEvent);

        verify(context1).runOnContext(anyObject());
    }

    @Test
    public void testGetConfigElement_EmptyPathPrefixes() throws Exception {
        when(zooKeeperOperationBuilders.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.usingWatcher(watcher)).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(elementPath)).thenReturn(getDataBuilder);
        when(getDataBuilder.build()).thenReturn(operation);

        when(configurator.getPathPrefixes()).thenReturn(null);

        DefaultConfiguratorHelper target = getTarget();
        target.getConfigElement(elementPath, watcher, event -> {
            assertEquals(data, event.result().asBytes());
        });

        verify(zooKeeperClient).execute(eq(operation), handlerCuratorEvent.capture());
        when(asyncResultCuratorEvent.result()).thenReturn(event);
        when(event.getData()).thenReturn(data);
        handlerCuratorEvent.getValue().handle(asyncResultCuratorEvent);
    }

    @Test
    public void testGetConfigElement_EmptyResults() throws Exception {
        when(zooKeeperOperationBuilders.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.usingWatcher(watcher)).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(elementPath)).thenReturn(getDataBuilder);
        when(getDataBuilder.build()).thenReturn(operation);

        DefaultConfiguratorHelper target = getTarget();
        target.getConfigElement(elementPath, watcher, event -> {
            assertNull(event.result().asBytes());
        });

        verify(zooKeeperClient).execute(eq(operation), handlerCuratorEvent.capture());
        when(asyncResultCuratorEvent.result()).thenReturn(event);
        handlerCuratorEvent.getValue().handle(asyncResultCuratorEvent);
    }

    @Test
    public void testGetConfigElement_FailedResult() throws Exception {
        when(zooKeeperOperationBuilders.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.usingWatcher(watcher)).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(elementPath)).thenReturn(getDataBuilder);
        when(getDataBuilder.build()).thenReturn(operation);

        DefaultConfiguratorHelper target = getTarget();
        target.getConfigElement(elementPath, watcher, event -> {
            assertEquals(throwable, event.cause());
        });

        verify(zooKeeperClient).execute(eq(operation), handlerCuratorEvent.capture());
        when(asyncResultCuratorEvent.failed()).thenReturn(true);
        when(asyncResultCuratorEvent.cause()).thenReturn(throwable);
        handlerCuratorEvent.getValue().handle(asyncResultCuratorEvent);
    }
}
