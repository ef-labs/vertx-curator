package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.promises.Deferred;
import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.zookeeper.ConfiguratorClient;
import com.englishtown.vertx.zookeeper.ZooKeeperClient;
import com.englishtown.vertx.zookeeper.builders.ZooKeeperOperationBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.platform.Container;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public class DefaultConfiguratorClient implements ConfiguratorClient {

    private static final String ZOOKEEPER_PATH_ENVVAR = "zookeeper_paths";
    private static final String PATH_DELIMITER = "\\|";

    private final When when;
    private final ZooKeeperClient zooKeeperClient;
    private final ZooKeeperOperationBuilders zooKeeperOperationBuilders;
    private List<String> basePaths = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(DefaultConfiguratorClient.class);

    @Inject
    public DefaultConfiguratorClient(Container container, When when, ZooKeeperClient zooKeeperClient, ZooKeeperOperationBuilders zooKeeperOperationBuilders) {
        this.when = when;
        this.zooKeeperClient = zooKeeperClient;
        this.zooKeeperOperationBuilders = zooKeeperOperationBuilders;

        init(container);
    }

    private void init(Container container) {
        String basePathsString = container.env().get(ZOOKEEPER_PATH_ENVVAR);
        basePaths.addAll(Arrays.asList(basePathsString.split(PATH_DELIMITER)));
    }

    @Override
    public Promise<byte[]> getConfigElement(String elementPath) {

        if (elementPath == null) return when.resolve(null);

        // We use our list of base paths, appending the desired path to each one, and go looking for each. Ultimately
        // we will resolve with the first non-null response we get. This means that the order of the base paths is important,
        // as the most desired base path should be the first in the list and the least desired last.

        return when.all(getAllForPaths(basePaths, elementPath)).then(
            allResponses -> {
                for (byte[] value : allResponses) {
                    if (value != null) {

                        return when.resolve(value);
                    }
                }

                // We didn't find a value that wasn't null, so resolve with null
                return when.resolve(null);
            }
        );
   }

    private List<Promise<byte[]>> getAllForPaths(List<String> paths, String elementPath) {
        List<Promise<byte[]>> all = new ArrayList<>();

        paths.forEach(path -> {
            Deferred<byte[]> d = when.defer();

            zooKeeperClient.execute(zooKeeperOperationBuilders.getData().forPath(path + elementPath).build(), asyncResult -> {
                        if (asyncResult.succeeded()) {
                            d.getResolver().resolve(asyncResult.result().getData());
                        } else {
                            d.getResolver().reject(asyncResult.cause());
                        }
                    }
            );

           all.add(d.getPromise());
        });

        return all;
    }
}
