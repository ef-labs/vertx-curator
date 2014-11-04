package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ZooKeeperConfigurator;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Container;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonConfigZooKeeperConfiguratorTest {

    @Mock
    Container container;

    @Mock
    Handler<AsyncResult<Void>> handler;

    @Test
    public void testJsonConfigZooKeeperConfigurator() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // connection_string
        JsonObject connStr = new JsonObject();
        zookeeper.putObject("connection_string", connStr);
        zookeeper.putString("connection_string", "cs");

        // retry
        JsonObject retry = new JsonObject();
        zookeeper.putObject("retry", retry);
        retry.putString("type", "org.apache.curator.retry.RetryNTimes");

        // auth
        JsonObject auth = new JsonObject();
        zookeeper.putObject("auth", auth);
        auth.putString("scheme", "digest");
        auth.putString("username", "name");
        auth.putString("password", "pass");

        // path_prefixes
        JsonArray pathPrefixes = new JsonArray();
        zookeeper.putArray("path_prefixes", pathPrefixes);
        pathPrefixes.add("a")
                .add("b");

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeper.putObject("ensemble", ensemble);
        ensemble.putString("name", "org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider");
        JsonArray hosts = new JsonArray();
        ensemble.putArray("hosts", hosts);
        hosts.add("127.0.0.1");

        when(container.config()).thenReturn(config);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(container);

        // asserts

        assertEquals("cs", target.getConnectionString());

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);

        ZooKeeperConfigurator.AuthPolicy authPolicy = target.getAuthPolicy();
        assertEquals("digest", authPolicy.geScheme());
        assertEquals("name:pass", authPolicy.getAuth());

        List<String> pathPrefixList = target.getPathPrefixes();
        assertEquals("a", pathPrefixList.get(0));
        assertEquals("b", pathPrefixList.get(1));

        EnsembleProvider ensembleProvider = target.getEnsembleProvider();
        assertNotNull(ensembleProvider);

        target.onReady(handler);
        verify(handler).handle(anyObject());
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_RetryOneTime() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // retry
        JsonObject retry = new JsonObject();
        zookeeper.putObject("retry", retry);
        retry.putString("type", "org.apache.curator.retry.RetryOneTime");

        when(container.config()).thenReturn(config);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(container);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_RetryUntilElapsedTime() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // retry
        JsonObject retry = new JsonObject();
        zookeeper.putObject("retry", retry);
        retry.putString("type", "org.apache.curator.retry.RetryUntilElapsed");

        when(container.config()).thenReturn(config);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(container);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_ExponentialBackoffRetry() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // retry
        JsonObject retry = new JsonObject();
        zookeeper.putObject("retry", retry);
        retry.putString("type", "org.apache.curator.retry.ExponentialBackoffRetry");

        when(container.config()).thenReturn(config);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(container);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_BoundedExponentialBackoffRetry() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // retry
        JsonObject retry = new JsonObject();
        zookeeper.putObject("retry", retry);
        retry.putString("type", "org.apache.curator.retry.BoundedExponentialBackoffRetry");

        when(container.config()).thenReturn(config);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(container);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_DefaultRetryPolicy() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // retry
        JsonObject retry = new JsonObject();
        zookeeper.putObject("retry", retry);
        retry.putString("type", null);

        when(container.config()).thenReturn(config);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(container);

        RetryPolicy retryPolicy = target.getRetryPolicy();
        assertNotNull(retryPolicy);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_AuthAuth() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // auth
        JsonObject auth = new JsonObject();
        zookeeper.putObject("auth", auth);
        auth.putString("scheme", "auth");
        auth.putString("auth", "test_auth");

        when(container.config()).thenReturn(config);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(container);

        ZooKeeperConfigurator.AuthPolicy authPolicy = target.getAuthPolicy();
        assertEquals("auth", authPolicy.geScheme());
        assertEquals("test_auth", authPolicy.getAuth());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigZooKeeperConfigurator_InvalidPathPrefix() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // path_prefixes
        JsonArray pathPrefixes = new JsonArray();
        zookeeper.putArray("path_prefixes", pathPrefixes);
        pathPrefixes.add("a")
                .add(1);

        when(container.config()).thenReturn(config);

        new JsonConfigZooKeeperConfigurator(container);
    }

    @Test
    public void testJsonConfigZooKeeperConfigurator_EmptyEnsemble() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeper.putObject("ensemble", ensemble);

        when(container.config()).thenReturn(config);

        JsonConfigZooKeeperConfigurator target = new JsonConfigZooKeeperConfigurator(container);
        assertNull(target.getEnsembleProvider());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigZooKeeperConfigurator_InvalidEnsembleHosts() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeper.putObject("ensemble", ensemble);
        ensemble.putString("name", "org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider");

        JsonArray hosts = new JsonArray();
        ensemble.putArray("hosts", hosts);
        hosts.add(1);

        when(container.config()).thenReturn(config);

        new JsonConfigZooKeeperConfigurator(container);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigZooKeeperConfigurator_EmptyEnsembleHosts() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeper.putObject("ensemble", ensemble);
        ensemble.putString("name", "org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider");
        JsonArray hosts = new JsonArray();
        ensemble.putArray("hosts", hosts);

        when(container.config()).thenReturn(config);

        new JsonConfigZooKeeperConfigurator(container);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testJsonConfigZooKeeperConfigurator_InvalidEnsembleName() throws Exception {

        // the entire config json
        JsonObject config = new JsonObject();

        // zookeeper
        JsonObject zookeeper = new JsonObject();
        config.putObject("zookeeper", zookeeper);

        // ensemble
        JsonObject ensemble = new JsonObject();
        zookeeper.putObject("ensemble", ensemble);
        ensemble.putString("name", "not_exists");

        when(container.config()).thenReturn(config);

        new JsonConfigZooKeeperConfigurator(container);
    }

}