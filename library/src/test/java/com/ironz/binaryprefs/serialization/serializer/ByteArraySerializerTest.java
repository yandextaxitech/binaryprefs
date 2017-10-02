package com.ironz.binaryprefs.serialization.serializer;

import org.junit.Test;

import static org.junit.Assert.*;

public final class ByteArraySerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private final ByteArraySerializer serializer = new ByteArraySerializer();
    private final byte[] value = new byte[]{1, 2, 3, 4, 5, 6, 7};

    @Test
    public void byteArrayConvert() {
        byte[] bytes = serializer.serialize(value);
        byte[] restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), 1);
        assertArrayEquals(value, restored);
    }

    @Test
    public void byteArrayIncorrectFlag() {
        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}