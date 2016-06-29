package com.englishtown.vertx.curator.builders;

/**
 * Curator operation builder to get data
 */
public interface GetDataBuilder extends
        CuratorOperationBuilder,
        Pathable<GetDataBuilder>,
        Watchable<GetDataBuilder> {

}
