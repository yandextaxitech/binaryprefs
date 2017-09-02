package com.ironz.binaryprefs.serialization.serializer;

import org.junit.Test;

import static org.junit.Assert.*;

public final class FloatSerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private final FloatSerializer serializer = new FloatSerializer();

    @Test
    public void floatConvert() {
        float value = 53.123f;

        byte[] bytes = serializer.serialize(value);

        float restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);
        assertEquals(value, restored, .0);
    }

    @Test
    public void floatIncorrectFlag() {
        float value = 53.123f;

        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}