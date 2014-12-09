package com.englishtown.vertx.zookeeper.builders;

/**
 * A pathable operation builder
 */
public interface Pathable<T> {

    T forPath(String path);

}
