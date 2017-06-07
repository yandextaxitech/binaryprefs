package com.ironz.binaryprefs.serialization;

import org.junit.Test;

import static org.junit.Assert.*;

public class ShortSerializerImplTest {

    private static final byte INCORRECT_FLAG = 0;

    private final Serializer<Short> serializer = new ShortSerializerImpl();

    @Test
    public void booleanConvert() {
        short value = Short.MAX_VALUE;

        byte[] bytes = serializer.serialize(value);
        short restored = serializer.deserialize(Serializer.EMPTY_KEY, bytes);

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