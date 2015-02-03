package com.englishtown.vertx.zookeeper.builders;

/**
 * ZooKeeper operation builder to set ACLs
 */
public interface SetACLBuilder extends
        ZooKeeperOperationBuilder,
        Pathable<SetACLBuilder>,
        Versionable<SetACLBuilder>,
        ACLable<SetACLBuilder> {

}
