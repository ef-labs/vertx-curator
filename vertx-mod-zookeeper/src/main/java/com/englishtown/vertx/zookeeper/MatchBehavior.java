package com.englishtown.vertx.zookeeper;

/**
 */
public enum MatchBehavior {
    /** Means the ConfiguratorHelper will return the first znode that matches the path **/
    FIRST,
    /** Means the ConfiguratorHelper will return the first znode that matches the path and has a non-null value **/
    FIRST_NOTNULL;
}
