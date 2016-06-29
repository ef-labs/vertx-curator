package com.englishtown.vertx.curator.builders;

/**
 * Curator operation builder to set ACLs
 */
public interface SetACLBuilder extends
        CuratorOperationBuilder,
        Pathable<SetACLBuilder>,
        Versionable<SetACLBuilder>,
        ACLable<SetACLBuilder> {

}
