package com.englishtown.vertx.zookeeper.builders;

/**
 * An operation builder with data
 */
public interface Dataable<T> {

    T data(byte[] data);

}
