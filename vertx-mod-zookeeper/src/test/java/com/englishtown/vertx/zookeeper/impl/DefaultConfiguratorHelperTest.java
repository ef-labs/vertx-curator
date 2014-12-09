package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.junit.Before;
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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfiguratorHelperTest {

    private DefaultConfiguratorHelper configuratorHelper;
    private String elementPath = "test/path";

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
    Context context;
    @Mock
    AsyncResult<Void> asyncResultOnReady;
    @Mock
    CuratorEvent event;
    @Mock
    com.englishtown.vertx.zookeeper.builders.GetDataBuilder getDataBuilder;
    @Mock
    ZooKeeperOperation operation;
    @Mock
    Throwable throwable;
    @Mock
    AsyncResult<CuratorEvent> curatorEventAsyncResult;

    @Captor
    ArgumentCaptor<Handler<AsyncResult<Void>>> onReadyCaptor;
    @Captor
    ArgumentCaptor<Handler<AsyncResult<CuratorEvent>>> curatorEventHandlerCaptor;

    byte[] data = new byte[1];

    @Before
    public void setUp() throws Exception {

        when(vertx.currentContext()).thenReturn(context);
        when(zooKeeperOperationBuilders.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.usingWatcher(any())).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(any())).thenReturn(getDataBuilder);
        when(getDataBuilder.build()).thenReturn(operation);
        when(curatorEventAsyncResult.result()).thenReturn(event);
        when(event.getData()).thenReturn(data);

        configuratorHelper = new DefaultConfiguratorHelper(configurator, zooKeeperClient, zooKeeperOperationBuilders, vertx);

        verify(configurator).onReady(onReadyCaptor.capture());
        onReadyCaptor.getValue().handle(asyncResultOnReady);

    }

    @Test
    public void testGetConfigElement_NullPath() throws Exception {
        configuratorHelper.getConfigElement(null, result -> {
            assertThat(result.cause(), instanceOf(IllegalArgumentException.class));
        });
    }

    @Test
    public void testGetConfigElement_RunOnExternalContext() throws Exception {

        when(vertx.currentContext()).thenReturn(context).thenReturn(mock(Context.class));

        configuratorHelper.getConfigElement(elementPath, watcher, result -> {
            // Should not be called
            fail();
        });

        verify(zooKeeperClient).execute(eq(operation), curatorEventHandlerCaptor.capture());
        curatorEventHandlerCaptor.getValue().handle(curatorEventAsyncResult);

        verify(context).runOnContext(any());
    }

    @Test
    public void testGetConfigElement_NullPathSuffixes() throws Exception {

        when(configurator.getPathSuffixes()).thenReturn(null);

        configuratorHelper.getConfigElement(elementPath, watcher, result -> {
            assertEquals(data, result.result().asBytes());
        });

        verify(zooKeeperClient).execute(eq(operation), curatorEventHandlerCaptor.capture());
        curatorEventHandlerCaptor.getValue().handle(curatorEventAsyncResult);
    }

    @Test
    public void testGetConfigElement_NullResults() throws Exception {

        when(event.getData()).thenReturn(null);

        configuratorHelper.getConfigElement(elementPath, watcher, result -> {
            assertFalse(result.result().hasValue());
        });

        verify(zooKeeperClient).execute(eq(operation), curatorEventHandlerCaptor.capture());
        curatorEventHandlerCaptor.getValue().handle(curatorEventAsyncResult);
    }

    @Test
    public void testGetConfigElement_FailedResult() throws Exception {

        when(curatorEventAsyncResult.failed()).thenReturn(true);
        when(curatorEventAsyncResult.cause()).thenReturn(throwable);

        configuratorHelper.getConfigElement(elementPath, watcher, result -> {
            assertEquals(throwable, result.cause());
        });

        verify(zooKeeperClient).execute(eq(operation), curatorEventHandlerCaptor.capture());
        curatorEventHandlerCaptor.getValue().handle(curatorEventAsyncResult);
    }
}
