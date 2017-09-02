package com.ironz.binaryprefs.serialization.serializer;

import org.junit.Test;

import static org.junit.Assert.*;

public final class CharSerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private final CharSerializer serializer = new CharSerializer();

    @Test
    public void booleanConvert() {
        char value = Character.MAX_VALUE;

        byte[] bytes = serializer.serialize(value);
        char restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);
        assertEquals(value, restored);
    }

    @Test
    public void booleanIncorrectFlag() {
        char value = Character.MAX_VALUE;

        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}