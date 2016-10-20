package com.englishtown.vertx.curator.impl;

import com.englishtown.vertx.curator.CuratorConfigurator;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.englishtown.vertx.curator.impl.JsonConfigCuratorConfigurator.FIELD_CURATOR;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonConfigCuratorConfiguratorTest {

    private JsonObject config;

    @Mock
    private Vertx vertx;
    @Mock
    private Context context;
    @Mock
    private Handler<AsyncResult<Void>> handler;

    @Before
    public void setUp() throws Exception {

        JsonObject config = new JsonObject();
        this.config = new JsonObject();
        config.put(FIELD_CURATOR, this.config);

        when(vertx.getOrCreateContext()).thenReturn(context);
        when(context.config()).thenReturn(config);

    }

    @Test
    public void testJsonConfigCuratorConfigurator() throws Exception {

        // connection_string
        config.put(JsonConfigCuratorConfigurator.FIELD_CONNECTION_STRING, "cs");

        // retry
        JsonObject retry = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.RetryNTimes");

        // auth
        JsonObject auth = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_AUTH, auth);
        auth.put("scheme", "digest");
        auth.put("username", "name");
        auth.put("password", "pass");

        // path_suffixes
        JsonArray pathSuffixes = new JsonArray();
        config.put(JsonConfigCuratorConfigurator.FIELD_PATH_SUFFIXES, pathSuffixes);
        pathSuffixes.add("a")
                .add("b");

        // ensemble
        JsonObject ensemble = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", "org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider");
        JsonArray hosts = new JsonArray();
        ensemble.put("hosts", hosts);
        hosts.add("127.0.0.1");


        JsonConfigCuratorConfigurator target = new JsonConfigCuratorConfigurator(vertx);

        // asserts

        assertEquals("cs", target.getConnectionString());

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);

        CuratorConfigurator.AuthPolicy authPolicy = target.getAuthPolicy();
        assertEquals("digest", authPolicy.geScheme());
        assertEquals("name:pass", authPolicy.getAuth());

        List<String> pathSuffixList = target.getPathSuffixes();
        assertEquals("a", pathSuffixList.get(0));
        assertEquals("b", pathSuffixList.get(1));

        EnsembleProvider ensembleProvider = target.getEnsembleProvider();
        assertNotNull(ensembleProvider);

        target.onReady(handler);
        verify(handler).handle(anyObject());
    }

    @Test
    public void testJsonConfigCuratorConfigurator_RetryOneTime() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.RetryOneTime");

        JsonConfigCuratorConfigurator target = new JsonConfigCuratorConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigCuratorConfigurator_RetryUntilElapsedTime() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.RetryUntilElapsed");

        JsonConfigCuratorConfigurator target = new JsonConfigCuratorConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigCuratorConfigurator_ExponentialBackoffRetry() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.ExponentialBackoffRetry");

        JsonConfigCuratorConfigurator target = new JsonConfigCuratorConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigCuratorConfigurator_BoundedExponentialBackoffRetry() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.BoundedExponentialBackoffRetry");

        JsonConfigCuratorConfigurator target = new JsonConfigCuratorConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigCuratorConfigurator_DefaultRetryPolicy() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_RETRY, retry);
        retry.put("type", (String) null);

        JsonConfigCuratorConfigurator target = new JsonConfigCuratorConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigCuratorConfigurator_AuthAuth() throws Exception {

        // auth
        JsonObject auth = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_AUTH, auth);
        auth.put("scheme", "auth");
        auth.put("auth", "test_auth");

        JsonConfigCuratorConfigurator target = new JsonConfigCuratorConfigurator(vertx);

        CuratorConfigurator.AuthPolicy authPolicy = target.getAuthPolicy();
        assertEquals(JsonConfigCuratorConfigurator.FIELD_AUTH, authPolicy.geScheme());
        assertEquals("test_auth", authPolicy.getAuth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigCuratorConfigurator_InvalidPathSuffix() throws Exception {

        // path_suffixes
        JsonArray pathSuffixes = new JsonArray();
        config.put(JsonConfigCuratorConfigurator.FIELD_PATH_SUFFIXES, pathSuffixes);
        pathSuffixes.add("a")
                .add(1);

        new JsonConfigCuratorConfigurator(vertx);
    }

    @Test
    public void testJsonConfigCuratorConfigurator_EmptyEnsemble() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_ENSEMBLE, ensemble);

        JsonConfigCuratorConfigurator target = new JsonConfigCuratorConfigurator(vertx);
        assertNull(target.getEnsembleProvider());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigCuratorConfigurator_InvalidEnsembleHosts() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", "org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider");

        JsonArray hosts = new JsonArray();
        ensemble.put("hosts", hosts);
        hosts.add(1);

        new JsonConfigCuratorConfigurator(vertx);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigCuratorConfigurator_EmptyEnsembleHosts() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", "org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider");
        JsonArray hosts = new JsonArray();
        ensemble.put("hosts", hosts);

        new JsonConfigCuratorConfigurator(vertx);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigCuratorConfigurator_InvalidEnsembleName() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", "not_exists");

        new JsonConfigCuratorConfigurator(vertx);
    }

    @Test
    public void testJsonConfigCuratorConfigurator_FixedEnsemble() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", FixedEnsembleProvider.class.getName());

        JsonConfigCuratorConfigurator configurator = new JsonConfigCuratorConfigurator(vertx);
        assertTrue(configurator.getEnsembleProvider() instanceof FixedEnsembleProvider);
    }

    @Test
    public void testJsonConfigCuratorConfigurator_ExhibitorEnsemble() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        config.put(JsonConfigCuratorConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", ExhibitorEnsembleProvider.class.getName());
        ensemble.put("hosts", new JsonArray().add("host1").add("host2"));

        JsonConfigCuratorConfigurator configurator = new JsonConfigCuratorConfigurator(vertx);
        assertTrue(configurator.getEnsembleProvider() instanceof ExhibitorEnsembleProvider);
    }

}