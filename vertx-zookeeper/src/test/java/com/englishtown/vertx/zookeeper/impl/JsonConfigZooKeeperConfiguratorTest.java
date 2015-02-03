package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonConfigZooKeeperConfiguratorTest {

    private JsonObject zookeeperConfig;

    @Mock
    private Vertx vertx;
    @Mock
    private Context context;
    @Mock
    private Handler<AsyncResult<Void>> handler;

    @Before
    public void setUp() throws Exception {

        JsonObject config = new JsonObject();
        zookeeperConfig = new JsonObject();
        config.put("zookeeper", zookeeperConfig);

        when(vertx.getOrCreateContext()).thenReturn(context);
        when(context.config()).thenReturn(config);

    }

    @Test
    public void testJsonConfigZooKeeperConfigurator() throws Exception {

        // connection_string
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_CONNECTION_STRING, "cs");

        // retry
        JsonObject retry = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.RetryNTimes");

        // auth
        JsonObject auth = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_AUTH, auth);
        auth.put("scheme", "digest");
        auth.put("username", "name");
        auth.put("password", "pass");

        // path_suffixes
        JsonArray pathSuffixes = new JsonArray();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_PATH_SUFFIXES, pathSuffixes);
        pathSuffixes.add("a")
                .add("b");

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", "org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider");
        JsonArray hosts = new JsonArray();
        ensemble.put("hosts", hosts);
        hosts.add("127.0.0.1");


        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(vertx);

        // asserts

        assertEquals("cs", target.getConnectionString());

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);

        ZooKeeperConfigurator.AuthPolicy authPolicy = target.getAuthPolicy();
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
    public void testJsonConfigZooKeeperConfigurator_RetryOneTime() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.RetryOneTime");

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_RetryUntilElapsedTime() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.RetryUntilElapsed");

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_ExponentialBackoffRetry() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.ExponentialBackoffRetry");

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_BoundedExponentialBackoffRetry() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_RETRY, retry);
        retry.put("type", "org.apache.curator.retry.BoundedExponentialBackoffRetry");

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_DefaultRetryPolicy() throws Exception {

        // retry
        JsonObject retry = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_RETRY, retry);
        retry.put("type", (String) null);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(vertx);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_AuthAuth() throws Exception {

        // auth
        JsonObject auth = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_AUTH, auth);
        auth.put("scheme", "auth");
        auth.put("auth", "test_auth");

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(vertx);

        ZooKeeperConfigurator.AuthPolicy authPolicy = target.getAuthPolicy();
        assertEquals(JsonConfigZooKeeperConfigurator.FIELD_AUTH, authPolicy.geScheme());
        assertEquals("test_auth", authPolicy.getAuth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigZooKeeperConfigurator_InvalidPathSuffix() throws Exception {

        // path_suffixes
        JsonArray pathSuffixes = new JsonArray();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_PATH_SUFFIXES, pathSuffixes);
        pathSuffixes.add("a")
                .add(1);

        new JsonConfigZooKeeperConfigurator(vertx);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_EmptyEnsemble() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_ENSEMBLE, ensemble);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(vertx);
        assertNull(target.getEnsembleProvider());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigZooKeeperConfigurator_InvalidEnsembleHosts() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", "org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider");

        JsonArray hosts = new JsonArray();
        ensemble.put("hosts", hosts);
        hosts.add(1);

        new JsonConfigZooKeeperConfigurator(vertx);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigZooKeeperConfigurator_EmptyEnsembleHosts() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", "org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider");
        JsonArray hosts = new JsonArray();
        ensemble.put("hosts", hosts);

        new JsonConfigZooKeeperConfigurator(vertx);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigZooKeeperConfigurator_InvalidEnsembleName() throws Exception {

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeperConfig.put(JsonConfigZooKeeperConfigurator.FIELD_ENSEMBLE, ensemble);
        ensemble.put("name", "not_exists");

        new JsonConfigZooKeeperConfigurator(vertx);
    }

}