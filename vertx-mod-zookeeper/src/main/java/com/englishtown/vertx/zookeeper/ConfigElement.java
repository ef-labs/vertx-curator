package com.englishtown.vertx.zookeeper;

import org.apache.curator.framework.api.CuratorEvent;
import org.vertx.java.core.json.JsonObject;

/**
 */
public interface ConfigElement {

    /**
     * Will convert this configuration element into a String.
     * If the original result is null, this method will also return null.
     *
     * @return String
     */
    String asString();

    /**
     * Will attempt to convert this configuration element into an Integer. If conversion isn't possible then an
     * {@link java.lang.IllegalArgumentException} will be thrown.
     *
     * If the original result is null, this method will also return null.
     *
     * @return Integer
     */
    Integer asInteger();

    /**
     * Will attempt to convert this configuration element into a long. If conversion isn't possible then an
     * {@link java.lang.IllegalArgumentException} will be thrown.
     *
     * If the original result is null, this method will also return null.
     *
     * @return long
     */
    Long asLong();

    /**
     * Will attempt to convert this configuration element into an {@link org.vertx.java.core.json.JsonObject}.
     * If conversion isn't possible then an {@link java.lang.IllegalArgumentException} will be thrown.
     *
     * If the original result is null, this method will also return null.
     *
     * @return JsonObject
     */
    JsonObject asJsonObject();

    /**
     * Will attempt to convert this configuration element into a boolean. Conversion is not considered possible if the
     * backing byte array has more than 1 byte in it or if that 1 byte is not equal to either 0 or 1.
     *
     * If conversion isn't possible then an {@link java.lang.IllegalArgumentException} will be thrown.
     *
     * If the original result is null, this method will also return null.
     *
     * @return boolean
     */
    Boolean asBoolean();

    /**
     * Will return the configuration element as a byte array. If the original result is null, this method will also
     * return null.
     *
     * @return byte[]
     */
    byte[] asBytes();

    /**
     * Returns the {@link org.apache.curator.framework.api.CuratorEvent} object that provides the raw result of
     * the config element request.
     *
     * @return CuratorEvent
     */
    CuratorEvent getCuratorEvent();
}
