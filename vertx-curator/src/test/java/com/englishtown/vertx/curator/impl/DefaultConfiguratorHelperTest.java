package com.englishtown.vertx.curator.impl;

import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorConfigurator;
import com.englishtown.vertx.curator.CuratorOperation;
import com.englishtown.vertx.curator.builders.CuratorOperationBuilders;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

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
    CuratorConfigurator configurator;
    @Mock
    CuratorClient curatorClient;
    @Mock
    CuratorOperationBuilders curatorOperationBuilders;
    @Mock
    Vertx vertx;
    @Mock
    Context context;
    @Mock
    AsyncResult<Void> asyncResultOnReady;
    @Mock
    CuratorEvent event;
    @Mock
    com.englishtown.vertx.curator.builders.GetDataBuilder getDataBuilder;
    @Mock
    CuratorOperation operation;
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

        when(vertx.getOrCreateContext()).thenReturn(context);
        when(curatorOperationBuilders.getData()).thenReturn(getDataBuilder);
        when(getDataBuilder.usingWatcher(any())).thenReturn(getDataBuilder);
        when(getDataBuilder.forPath(any())).thenReturn(getDataBuilder);
        when(getDataBuilder.build()).thenReturn(operation);
        when(curatorEventAsyncResult.result()).thenReturn(event);
        when(event.getData()).thenReturn(data);

        configuratorHelper = new DefaultConfiguratorHelper(configurator, curatorClient, curatorOperationBuilders, vertx);

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

        when(vertx.getOrCreateContext()).thenReturn(context).thenReturn(mock(Context.class));

        configuratorHelper.getConfigElement(elementPath, watcher, result -> {
            // Should not be called
            fail();
        });

        verify(curatorClient).execute(eq(operation), curatorEventHandlerCaptor.capture());
        curatorEventHandlerCaptor.getValue().handle(curatorEventAsyncResult);

        verify(context).runOnContext(any());
    }

    @Test
    public void testGetConfigElement_NullPathSuffixes() throws Exception {

        when(configurator.getPathSuffixes()).thenReturn(null);

        configuratorHelper.getConfigElement(elementPath, watcher, result -> {
            assertEquals(data, result.result().asBytes());
        });

        verify(curatorClient).execute(eq(operation), curatorEventHandlerCaptor.capture());
        curatorEventHandlerCaptor.getValue().handle(curatorEventAsyncResult);
    }

    @Test
    public void testGetConfigElement_NullResults() throws Exception {

        when(event.getData()).thenReturn(null);

        configuratorHelper.getConfigElement(elementPath, watcher, result -> {
            assertFalse(result.result().hasValue());
        });

        verify(curatorClient).execute(eq(operation), curatorEventHandlerCaptor.capture());
        curatorEventHandlerCaptor.getValue().handle(curatorEventAsyncResult);
    }

    @Test
    public void testGetConfigElement_FailedResult() throws Exception {

        when(curatorEventAsyncResult.failed()).thenReturn(true);
        when(curatorEventAsyncResult.cause()).thenReturn(throwable);

        configuratorHelper.getConfigElement(elementPath, watcher, result -> {
            assertEquals(throwable, result.cause());
        });

        verify(curatorClient).execute(eq(operation), curatorEventHandlerCaptor.capture());
        curatorEventHandlerCaptor.getValue().handle(curatorEventAsyncResult);
    }
}
