package com.englishtown.vertx.zookeeper.impl;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.apache.curator.framework.api.CuratorEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.vertx.java.core.json.JsonObject;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigElementTest {

    @Mock
    CuratorEvent curatorEvent;

    @Test
    public void testConversionToString() throws Exception {
        // Set up interactions
        when(curatorEvent.getData()).thenReturn("Test String".getBytes());

        // When we create a new instance of DefaultConfigElement
        DefaultConfigElement dce = new DefaultConfigElement(curatorEvent);

        // and then request the String version of our data
        String result = dce.asString();

        // Then we expect the string to contain our test string.
        assertEquals("Test String", result);
    }

    @Test
    public void testConversionToInteger() throws Exception {
        // Set up interactions
        when(curatorEvent.getData())
                .thenReturn(Ints.toByteArray(18271))
                .thenReturn("This is too long for an Integer".getBytes())
                .thenReturn(null);

        // 1. With a valid integer
        // When we create a new instance of DefaultConfigElement
        DefaultConfigElement dce = new DefaultConfigElement(curatorEvent);

        // and then request the Integer version of our data
        Integer result = dce.asInteger();

        // Then we expect the Integer to contain our test number.
        assertEquals(18271, result.intValue());


        // 2. With an invalid integer
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the Integer version of our data
        // we expect an exception to be thrown
        try {
            result = dce.asInteger();
            fail("No expection thrown for bad integer type");
        } catch (IllegalArgumentException iae) {
            // Do nothing, we're good.
        } catch (Exception e) {
            fail("Unknown exception thrown when getting value asInteger");
        }


        // 3. With a null
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the Integer version of our data
        result = dce.asInteger();

        // Then we expect it to be null
        assertNull(result);
    }

    @Test
    public void testConversionToLong() throws Exception {
        // Set up interactions
        when(curatorEvent.getData())
                .thenReturn(Longs.toByteArray(18271000000L))
                .thenReturn("This is too long for an Long".getBytes())
                .thenReturn(null);

        // 1. With a valid long
        // When we create a new instance of DefaultConfigElement
        DefaultConfigElement dce = new DefaultConfigElement(curatorEvent);

        // and then request the Long version of our data
        Long result = dce.asLong();

        // Then we expect the Long to contain our test number.
        assertEquals(18271000000L, result.longValue());


        // 2. With an invalid Long
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the Long version of our data
        // we expect an exception to be thrown
        try {
            result = dce.asLong();
            fail("No expection thrown for bad long type");
        } catch (IllegalArgumentException iae) {
            // Do nothing, we're good.
        } catch (Exception e) {
            fail("Unknown exception thrown when getting value asLong");
        }


        // 3. With a null
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the Long version of our data
        result = dce.asLong();

        // Then we expect it to be null
        assertNull(result);
    }

    @Test
    public void testConversionToJsonObject() throws Exception {
        // Set up interactions
        JsonObject jsonObject = new JsonObject().putString("test", "data").putNumber("testnum", 18271);

        when(curatorEvent.getData())
                .thenReturn(jsonObject.encode().getBytes())
                .thenReturn("This isn't JSON".getBytes())
                .thenReturn(null);

        // 1. With a valid json object
        // When we create a new instance of DefaultConfigElement
        DefaultConfigElement dce = new DefaultConfigElement(curatorEvent);

        // and then request the JsonObject version of our data
        JsonObject result = dce.asJsonObject();

        // Then we expect the JsonObject to contain our test json.
        assertEquals(jsonObject, result);


        // 2. With an invalid Json string
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the JSON version of our data
        // we expect an exception to be thrown
        try {
            result = dce.asJsonObject();
            fail("No expection thrown for bad json type");
        } catch (IllegalArgumentException iae) {
            // Do nothing, we're good.
        } catch (Exception e) {
            fail("Unknown exception thrown when getting value asJsonObject");
        }


        // 3. With a null
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the JSON version of our data
        result = dce.asJsonObject();

        // Then we expect it to be null
        assertNull(result);
    }

    @Test
    public void testGetBytes() throws Exception {
        // Set up interactions
        when(curatorEvent.getData())
                .thenReturn(new byte[] {1, 8, 2, 7, 1})
                .thenReturn(null);

        // 1. With a valid byte[]
        // When we create a new instance of DefaultConfigElement
        DefaultConfigElement dce = new DefaultConfigElement(curatorEvent);

        // and then request the bytes of our data
        byte[] result = dce.asBytes();

        // Then we expect the byte array to contain our test bytes.
        assertTrue(Arrays.equals(new byte[]{1, 8, 2, 7, 1}, result));

        // 2. With a null result
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the bytes of our data
        result = dce.asBytes();

        // Then we expect our result to be null
        assertNull(result);
    }

    @Test
    public void testConversionToBoolean() throws Exception {
        // Set up interactions
        when(curatorEvent.getData())
                .thenReturn(new byte[] {1})
                .thenReturn(new byte[] {2})
                .thenReturn("This isn't a boolean".getBytes())
                .thenReturn(null);

        // 1. With a valid boolean
        // When we create a new instance of DefaultConfigElement
        DefaultConfigElement dce = new DefaultConfigElement(curatorEvent);

        // and then request the boolean version of our data
        Boolean result = dce.asBoolean();

        // Then we expect the result to be true
        assertTrue(result);


        // 2. With a valid length byte[], but with an invalid value
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the boolean version of our data
        // we expect an exception to be thrown
        try {
            result = dce.asBoolean();
            fail("No expection thrown for bad boolean type");
        } catch (IllegalArgumentException iae) {
            // Do nothing, we're good.
        } catch (Exception e) {
            fail("Unknown exception thrown when getting value asBoolean");
        }


        // 3. With an invalid length byte[]
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the boolean version of our data
        // we expect an exception to be thrown
        try {
            result = dce.asBoolean();
            fail("No expection thrown for bad boolean type");
        } catch (IllegalArgumentException iae) {
            // Do nothing, we're good.
        } catch (Exception e) {
            fail("Unknown exception thrown when getting value asBoolean");
        }


        // 4. With a null
        // When we create a new instance of DefaultConfigElement
        dce = new DefaultConfigElement(curatorEvent);

        // and then request the JSON version of our data
        result = dce.asBoolean();

        // Then we expect it to be null
        assertNull(result);
    }

}
