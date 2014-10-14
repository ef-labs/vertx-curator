package com.englishtown.vertx.zookeeper.builders;

/**
 * A versionable operation builder
 */
public interface Versionable<T> {

    T withVersion(int version);

}
