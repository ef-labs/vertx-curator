package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
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
import org.vertx.java.core.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultZooKeeperClientTest {

    @Mock
    Vertx vertx;

    @Mock
    Context context;

    @Mock
    ZooKeeperConfigurator configurator;

    @Mock
    AsyncResult<Void> asyncResultOnReady;

    @Captor
    ArgumentCaptor<Handler<AsyncResult<Void>>> handlerCaptorOnReady;

    @Mock
    Handler<AsyncResult<Void>> handler;

    @Mock
    EnsembleProvider ensembleProvider;

    @Mock
    ZooKeeperOperation operation;

    @Mock
    Exception throwable;

    @Mock
    RetryPolicy retryPolicy;

    @Mock
    ZooKeeperConfigurator.AuthPolicy authPolicy;

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

    @Mock
    CuratorEvent curatorEvent;

    @Captor
    ArgumentCaptor<Handler<AsyncResult<CuratorEvent>>> handlerCaptorExecute;

    @Test
    public void testInitialization_OnReadyFailed() throws Exception {
        new DefaultZooKeeperClient(vertx, configurator).onReady(handler);

        verify(configurator).onReady(handlerCaptorOnReady.capture());
        when(asyncResultOnReady.failed()).thenReturn(true);
        handlerCaptorOnReady.getValue().handle(asyncResultOnReady);

        verify(handler).handle(eq(asyncResultOnReady));
    }

    @Test
    public void testExecute_WithException() throws Exception {
        DefaultZooKeeperClient target = new DefaultZooKeeperClient(vertx, configurator);
        verify(configurator).onReady(handlerCaptorOnReady.capture());
        when(configurator.getConnectionString()).thenReturn("cs");
        when(configurator.getAuthPolicy()).thenReturn(authPolicy);
        when(authPolicy.getAuth()).thenReturn("auth");
        when(configurator.getRetryPolicy()).thenReturn(retryPolicy);
        handlerCaptorOnReady.getValue().handle(asyncResultOnReady);

        doThrow(throwable).when(operation).execute(eq(target), anyObject());
        target.execute(operation, event -> {
            assertEquals(throwable, event.cause());
        });

        assertNotNull(target.getCuratorFramework());
    }

    @Test
    public void testExecute_Success() throws Exception {
        when(vertx.currentContext()).thenReturn(context);

        DefaultZooKeeperClient target = new DefaultZooKeeperClient(vertx, configurator);
        verify(configurator).onReady(handlerCaptorOnReady.capture());
        when(configurator.getConnectionString()).thenReturn("cs");
        when(configurator.getAuthPolicy()).thenReturn(authPolicy);
        when(authPolicy.getAuth()).thenReturn("auth");
        when(configurator.getRetryPolicy()).thenReturn(retryPolicy);
        handlerCaptorOnReady.getValue().handle(asyncResultOnReady);

        target.execute(operation, event -> {
            assertEquals(curatorEvent, event.result());
        });

        assertNotNull(target.getCuratorFramework());

        verify(operation).execute(eq(target), handlerCaptorExecute.capture());

        when(asyncResultExecute.result()).thenReturn(curatorEvent);
        handlerCaptorExecute.getValue().handle(asyncResultExecute);

        verify(context).runOnContext(handlerCaptorRunOnContext.capture());
        handlerCaptorRunOnContext.getValue().handle(null);
    }

    @Test
    public void testOnReady() throws Exception {
        DefaultZooKeeperClient target = new DefaultZooKeeperClient(vertx, configurator);
        verify(configurator).onReady(handlerCaptorOnReady.capture());
        when(configurator.getEnsembleProvider()).thenReturn(ensembleProvider);
        when(configurator.getRetryPolicy()).thenReturn(retryPolicy);
        handlerCaptorOnReady.getValue().handle(asyncResultOnReady);

        assertNotNull(target.getCuratorFramework());

        assertTrue(target.initialized());

        target.onReady(handler);
        verify(handler).handle(anyObject());
    }

    @Test
    public void testWrapWatcher_Success() throws Exception {
        when(vertx.currentContext()).thenReturn(context);

        DefaultZooKeeperClient target = new DefaultZooKeeperClient(vertx, configurator);
        verify(configurator).onReady(handlerCaptorOnReady.capture());
        when(configurator.getEnsembleProvider()).thenReturn(ensembleProvider);
        when(configurator.getRetryPolicy()).thenReturn(retryPolicy);
        handlerCaptorOnReady.getValue().handle(asyncResultOnReady);

        target.wrapWatcher(watcher).process(watchedEvent);
        verify(context).runOnContext(handlerCaptorRunOnContext.capture());
        handlerCaptorRunOnContext.getValue().handle(null);
        verify(watcher).process(watchedEvent);
    }

    @Test
    public void testWrapWatcher_OnException() throws Exception {
        when(vertx.currentContext()).thenReturn(context);

        DefaultZooKeeperClient target = new DefaultZooKeeperClient(vertx, configurator);
        verify(configurator).onReady(handlerCaptorOnReady.capture());
        when(configurator.getEnsembleProvider()).thenReturn(ensembleProvider);
        when(configurator.getAuthPolicy()).thenReturn(authPolicy);
        when(authPolicy.getAuth()).thenReturn("auth");
        when(configurator.getRetryPolicy()).thenReturn(retryPolicy);
        handlerCaptorOnReady.getValue().handle(asyncResultOnReady);

        doThrow(throwable).when(watcher).process(watchedEvent);
        target.wrapWatcher(watcher).process(watchedEvent);
        verify(context).runOnContext(handlerCaptorRunOnContext.capture());
        handlerCaptorRunOnContext.getValue().handle(null);
    }
}
