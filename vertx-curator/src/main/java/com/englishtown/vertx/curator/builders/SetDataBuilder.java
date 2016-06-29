package com.englishtown.vertx.curator.builders;

/**
 * Curator operation builder to set data
 */
public interface SetDataBuilder extends
        CuratorOperationBuilder,
        Pathable<SetDataBuilder>,
        Dataable<SetDataBuilder>,
        Versionable<SetDataBuilder> {
}
