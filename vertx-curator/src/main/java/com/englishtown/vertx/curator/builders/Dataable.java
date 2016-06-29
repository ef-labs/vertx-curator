package com.englishtown.vertx.curator.builders;

/**
 * An operation builder with data
 */
public interface Dataable<T> {

    T data(byte[] data);

}
