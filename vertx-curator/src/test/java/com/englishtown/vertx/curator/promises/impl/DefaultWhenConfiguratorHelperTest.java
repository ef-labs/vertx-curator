package com.englishtown.vertx.curator.promises.impl;

import com.englishtown.promises.Done;
import com.englishtown.promises.When;
import com.englishtown.promises.WhenFactory;
import com.englishtown.vertx.curator.ConfigElement;
import com.englishtown.vertx.curator.ConfiguratorHelper;
import com.englishtown.vertx.curator.MatchBehavior;
import org.apache.curator.framework.api.CuratorWatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultWhenConfiguratorHelperTest {

    @Mock
    ConfiguratorHelper helper;

    @Mock
    CuratorWatcher watcher;

    String path = "test/path";

    @Mock
    AsyncResult<ConfigElement> asyncResult;

    @Mock
    ConfigElement configElement;

    @Captor
    ArgumentCaptor<Handler<AsyncResult<ConfigElement>>> handlerCaptor;

    private Done<ConfigElement> done = new Done<>();

    @Test
    public void testGetConfigElement_Success() throws Exception {
        When when = WhenFactory.createSync();

        DefaultWhenConfiguratorHelper target = new DefaultWhenConfiguratorHelper(helper, when);

        target.getConfigElement(path, watcher).then(done.onFulfilled, done.onRejected);

        verify(helper).getConfigElement(eq(path), eq(watcher), eq(MatchBehavior.FIRST), handlerCaptor.capture());
        when(asyncResult.succeeded()).thenReturn(true);
        when(asyncResult.result()).thenReturn(configElement);
        handlerCaptor.getValue().handle(asyncResult);

        done.assertFulfilled();
        assertEquals(configElement, done.getValue());
    }

    @Test
    public void testGetConfigElement_Failed() throws Exception {
        When when = WhenFactory.createSync();

        DefaultWhenConfiguratorHelper target = new DefaultWhenConfiguratorHelper(helper, when);

        target.getConfigElement(path).then(done.onFulfilled, done.onRejected);

        verify(helper).getConfigElement(eq(path), eq((CuratorWatcher) null), eq(MatchBehavior.FIRST), handlerCaptor.capture());
        when(asyncResult.succeeded()).thenReturn(false);
        handlerCaptor.getValue().handle(asyncResult);

        done.assertRejected();
    }
}
