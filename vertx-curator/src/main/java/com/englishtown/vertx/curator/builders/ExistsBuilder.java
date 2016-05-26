package com.englishtown.vertx.curator.builders;

/**
 * Curator operation builder to check a z-node existence
 */
public interface ExistsBuilder extends
        CuratorOperationBuilder,
        Pathable<ExistsBuilder>,
        Watchable<ExistsBuilder> {
}
