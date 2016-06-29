package com.englishtown.vertx.curator.builders;

/**
 * A versionable operation builder
 */
public interface Versionable<T> {

    T withVersion(int version);

}
