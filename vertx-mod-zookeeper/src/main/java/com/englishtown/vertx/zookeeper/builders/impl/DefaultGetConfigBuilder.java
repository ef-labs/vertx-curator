package com.englishtown.vertx.zookeeper.builders.impl;

import com.englishtown.promises.Promise;
import com.englishtown.promises.When;
import com.englishtown.vertx.zookeeper.MatchBehavior;
import com.englishtown.vertx.zookeeper.ZooKeeperOperation;
import com.englishtown.vertx.zookeeper.builders.GetConfigBuilder;
import com.englishtown.vertx.zookeeper.builders.GetDataBuilder;
import com.englishtown.vertx.zookeeper.promises.WhenZooKeeperClient;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorWatcher;
import org.vertx.java.core.impl.DefaultFutureResult;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class DefaultGetConfigBuilder extends AbstractOperationBuilder<GetConfigBuilder> implements GetConfigBuilder {

    private String environment;
    private String locale;
    private String application;
    private MatchBehavior matchBehavior;
    private WhenZooKeeperClient whenZooKeeperClient;
    private DefaultZooKeeperOperationBuilders builders;
    private When when;

    private static final String DELIM = ".";

    public DefaultGetConfigBuilder(WhenZooKeeperClient whenZooKeeperClient,
                                   DefaultZooKeeperOperationBuilders builders,
                                   When when) {
        this.whenZooKeeperClient = whenZooKeeperClient;
        this.builders = builders;
        this.when = when;

        matchBehavior = MatchBehavior.FIRST;
    }

    @Override
    protected ZooKeeperOperation build(String path, CuratorWatcher watcher) {

        // Environment is mandatory. The other two are not, so if there is no environment we
        // throw an exception.
        if (Strings.isNullOrEmpty(environment)) {
            throw new IllegalArgumentException("Environment must be specified");
        }

        return (client, handler) -> {
            List<Promise<CuratorEvent>> promises = new ArrayList<>();
            promises.add(getNode(path + DELIM + environment));

            if (!Strings.isNullOrEmpty(locale)) {
                promises.add(getNode(path + DELIM + environment + DELIM + locale));

                if (!Strings.isNullOrEmpty(application)) {
                    promises.add(getNode(path + DELIM + environment + DELIM + locale + DELIM + application));
                }
            } else if (!Strings.isNullOrEmpty(application)) {
                promises.add(getNode(path + DELIM + environment + DELIM + application));
            }

            when.all(promises).then(
                    all -> {
                        List<CuratorEvent> allReversed = Lists.reverse(all);
                        for (CuratorEvent event : allReversed) {
                            if (event.getStat() != null) {
                                if (matchBehavior == MatchBehavior.FIRST) {
                                    // We need to read this one again, just to set the watcher, if we have one
                                    if (watcher != null) {
                                        getNode(event.getPath(), watcher).then(
                                                curatorEvent -> {
                                                    handler.handle(new DefaultFutureResult<>(curatorEvent));
                                                    return null;
                                                }
                                        );
                                    } else {
                                        handler.handle(new DefaultFutureResult<>(event));
                                    }
                                }
                            }
                        }

                        return null;
                    }
            ).otherwise(t -> {
                handler.handle(new DefaultFutureResult<>(t));
                return null;
            });
        };
    }

    private Promise<CuratorEvent> getNode(String path) {
        return getNode(path, null);
    }

    private Promise<CuratorEvent> getNode(String path, CuratorWatcher watcher) {
        GetDataBuilder builder = builders.getData().forPath(path).usingWatcher(watcher);

        return whenZooKeeperClient.execute(builder.build());
    }

    @Override
    public GetConfigBuilder environment(String environment) {
        this.environment = environment;

        return this;
    }

    @Override
    public GetConfigBuilder locale(String locale) {
        this.locale = locale;

        return this;
    }

    @Override
    public GetConfigBuilder application(String application) {
        this.application = application;

        return this;
    }

    @Override
    public GetConfigBuilder matchBehavior(MatchBehavior matchBehavior) {
        this.matchBehavior = matchBehavior;

        return this;
    }
}
