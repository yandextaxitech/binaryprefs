package com.ironz.binaryprefs.serialization.serializer;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerSerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private final IntegerSerializer serializer = new IntegerSerializer();

    @Test
    public void integerConvert() {
        int value = 53;

        byte[] bytes = serializer.serialize(value);
        int restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);
        assertEquals(value, restored);
    }

    @Test
    public void integerIncorrectFlag() {
        int value = 53;

        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}