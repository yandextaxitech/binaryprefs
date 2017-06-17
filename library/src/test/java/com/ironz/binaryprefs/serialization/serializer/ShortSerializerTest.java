package com.ironz.binaryprefs.serialization.serializer;

import org.junit.Test;

import static org.junit.Assert.*;

public class ShortSerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private final ShortSerializer serializer = new ShortSerializer();

    @Test
    public void booleanConvert() {
        short value = Short.MAX_VALUE;

        byte[] bytes = serializer.serialize(value);
        short restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(value));
        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);
        assertEquals(value, restored);
    }

    @Test
    public void booleanIncorrectFlag() {
        short value = Short.MAX_VALUE;

        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}