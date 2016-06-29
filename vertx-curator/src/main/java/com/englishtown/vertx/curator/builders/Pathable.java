package com.englishtown.vertx.curator.builders;

/**
 * A pathable operation builder
 */
public interface Pathable<T> {

    T forPath(String path);

}
