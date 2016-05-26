package com.englishtown.vertx.curator.builders;

/**
 * Curator operation builder to delete a z-node
 */
public interface DeleteBuilder extends
        CuratorOperationBuilder,
        Pathable<DeleteBuilder>,
        Versionable<DeleteBuilder> {

    DeleteBuilder deletingChildrenIfNeeded();

    DeleteBuilder guaranteed();

}
