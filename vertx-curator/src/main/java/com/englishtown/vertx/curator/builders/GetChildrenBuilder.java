package com.englishtown.vertx.curator.builders;

/**
 * Curator operation builder to get children
 */
public interface GetChildrenBuilder extends
        CuratorOperationBuilder,
        Pathable<GetChildrenBuilder>,
        Watchable<GetChildrenBuilder> {

}
