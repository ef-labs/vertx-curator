package com.englishtown.vertx.zookeeper.builders;

/**
 * ZooKeeper operation builder to get children
 */
public interface GetChildrenBuilder extends
        ZooKeeperOperationBuilder,
        Pathable<GetChildrenBuilder>,
        Watchable<GetChildrenBuilder> {

}
