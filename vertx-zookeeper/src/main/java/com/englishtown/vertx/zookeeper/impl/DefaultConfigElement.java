package com.englishtown.vertx.zookeeper.impl;

import com.englishtown.vertx.zookeeper.ConfigElement;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.apache.curator.framework.api.CuratorEvent;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 */
public class DefaultConfigElement implements ConfigElement {

    private final byte[] bytesResult;
    private CuratorEvent curatorEvent;

    public DefaultConfigElement(CuratorEvent curatorEvent) {
        this.curatorEvent = curatorEvent;
        bytesResult = (curatorEvent == null) ? null : curatorEvent.getData();
    }

    @Override
    public String asString() {
        if (bytesResult == null) return null;

        return new String(bytesResult);
    }

    @Override
    public Integer asInteger() {
        if (bytesResult == null) return null;

        // If we don't have 4 bytes, then we can't convert to an integer
        if (bytesResult.length != 4) throw new IllegalArgumentException("Cannot convert to an integer");

        return Ints.fromByteArray(bytesResult);
    }

    @Override
    public Long asLong() {
        if (bytesResult == null) return null;

        // If we don't have 8 bytes, then we can't convert to a long
        if (bytesResult.length != 8) throw new IllegalArgumentException("Cannot convert to a long");

        return Longs.fromByteArray(bytesResult);
    }

    @Override
    public JsonObject asJsonObject() {
        if (bytesResult == null) return null;

        try {
            return new JsonObject(new String(bytesResult));
        } catch (DecodeException e) {
            throw new IllegalArgumentException("Cannot convert to a JSON Object");
        }
    }

    @Override
    public JsonArray asJsonArray() {
        if (bytesResult == null) return null;

        try {
            return new JsonArray(new String(bytesResult));
        } catch (DecodeException e) {
            throw new IllegalArgumentException("Cannot convert to a JSON Array");
        }
    }

    @Override
    public Boolean asBoolean() {
        if (bytesResult == null) return null;

        // If we have more than 1 bte, we can't convert to a boolean
        if (bytesResult.length != 1 || bytesResult[0] > 1 || bytesResult[0] < 0) throw new IllegalArgumentException("Cannot convert to a boolean");

        return (bytesResult[0] == 1);
    }

    @Override
    public byte[] asBytes() {
        return bytesResult;
    }

    @Override
    public CuratorEvent getCuratorEvent() {
        return curatorEvent;
    }

    @Override
    public boolean hasValue() {
        return bytesResult != null;
    }
}
