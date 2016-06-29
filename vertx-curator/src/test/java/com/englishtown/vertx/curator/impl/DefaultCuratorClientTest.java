package com.englishtown.vertx.curator.impl;

import com.englishtown.vertx.curator.CuratorClient;
import com.englishtown.vertx.curator.CuratorConfigurator;
import com.englishtown.vertx.curator.CuratorOperation;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultCuratorClientTest {

    private DefaultCuratorClient client;

    @Mock
    Vertx vertx;
    @Mock
    Context context;
    @Mock
    CuratorConfigurator configurator;
    @Mock
    AsyncResult<Void> asyncResultOnReady;
    @Captor
    ArgumentCaptor<Handler<AsyncResult<Void>>> handlerCaptorOnReady;
    @Mock
    Handler<AsyncResult<Void>> handler;
    @Mock
    EnsembleProvider ensembleProvider;
    @Mock
    CuratorOperation operation;
    @Mock
    Exception throwable;
    @Mock
    RetryPolicy retryPolicy;
    @Mock
    CuratorConfigurator.AuthPolicy authPolicy;
    @Mock
    CuratorWatcher watcher;
    @Captor
    ArgumentCaptor<Handler<Void>> handlerCaptorRunOnContext;
    @Mock
    WatchedEvent watchedEvent;
    @Mock
    Logger logger;
    @Mock
    AsyncResult<CuratorEvent> asyncResultExecute;
    @Captor
    ArgumentCaptor<Handler<AsyncResult<CuratorEvent>>> handlerCaptorExecute;

    @Before
    public void setUp() throws Exception {

        when(vertx.getOrCreateContext()).thenReturn(context);

        when(configurator.getConnectionString()).thenReturn("cs");
        when(configurator.getAuthPolicy()).thenReturn(authPolicy);
        when(authPolicy.getAuth()).thenReturn("auth");
        when(ensembleProvider.getConnectionString()).thenReturn(JsonConfigCuratorConfigurator.DEFAULT_CONNECTION_STRING);
        when(configurator.getEnsembleProvider()).thenReturn(ensembleProvider);
        when(configurator.getRetryPolicy()).thenReturn(retryPolicy);

        client = new DefaultCuratorClient(vertx, configurator);
    }

    private void runOnReady() {
        verify(configurator).onReady(handlerCaptorOnReady.capture());
        handlerCaptorOnReady.getValue().handle(asyncResultOnReady);
    }

    @Test
    public void testOnReady() throws Exception {

        runOnReady();

        assertNotNull(client.getCuratorFramework());
        assertTrue(client.initialized());

        client.onReady(handler);
        verify(handler).handle(anyObject());

    }

    @Test
    public void testInitialization_OnReadyFailed() throws Exception {

        when(asyncResultOnReady.failed()).thenReturn(true);

        client.onReady(handler);
        runOnReady();

        verify(handler).handle(eq(asyncResultOnReady));

    }

    @Test
    public void testExecute_WithException() throws Exception {

        doThrow(throwable).when(operation).execute(eq(client), any());

        runOnReady();

        client.execute(operation, event -> assertEquals(throwable, event.cause()));
        assertNotNull(client.getCuratorFramework());

    }

    @Test
    public void testExecute_Success() throws Exception {

        CuratorEvent curatorEvent = mock(CuratorEvent.class);

        runOnReady();
        client.execute(operation, event -> assertEquals(curatorEvent, event.result()));

        assertNotNull(client.getCuratorFramework());

        verify(operation).execute(eq(client), handlerCaptorExecute.capture());

        when(asyncResultExecute.result()).thenReturn(curatorEvent);
        handlerCaptorExecute.getValue().handle(asyncResultExecute);

        verify(context).runOnContext(handlerCaptorRunOnContext.capture());
        handlerCaptorRunOnContext.getValue().handle(null);
    }

    @Test
    public void testUsingNamespace() throws Exception {

        runOnReady();

        CuratorClient namespaceClient = client.usingNamespace("test");
        assertNotNull(namespaceClient);
        assertNotNull(namespaceClient.getCuratorFramework());

    }

    @Test
    public void testWrapWatcher_Success() throws Exception {

        runOnReady();

        client.wrapWatcher(watcher).process(watchedEvent);
        verify(context).runOnContext(handlerCaptorRunOnContext.capture());
        handlerCaptorRunOnContext.getValue().handle(null);
        verify(watcher).process(watchedEvent);

    }

    @Test
    public void testWrapWatcher_OnException() throws Exception {

        runOnReady();

        doThrow(throwable).when(watcher).process(watchedEvent);
        client.wrapWatcher(watcher).process(watchedEvent);
        verify(context).runOnContext(handlerCaptorRunOnContext.capture());
        handlerCaptorRunOnContext.getValue().handle(null);
    }

}
