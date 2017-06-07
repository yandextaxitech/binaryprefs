package com.ironz.binaryprefs.serialization;

import org.junit.Test;

import static org.junit.Assert.*;

public class FloatSerializerImplTest {

    private static final byte INCORRECT_FLAG = 0;

    private final Serializer<Float> serializer = new FloatSerializerImpl();

    @Test
    public void floatConvert() {
        float value = 53.123f;

        byte[] bytes = serializer.serialize(value);

        float restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(value));
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