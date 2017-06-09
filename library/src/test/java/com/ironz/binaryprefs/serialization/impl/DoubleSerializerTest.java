package com.ironz.binaryprefs.serialization.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleSerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private final DoubleSerializer serializer = new DoubleSerializer();

    @Test
    public void doubleConvert() {
        double value = 53.123;

        byte[] bytes = serializer.serialize(value);
        double restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(value));
        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);
        assertEquals(value, restored, .0);
    }

    @Test
    public void doubleIncorrectFlag() {
        double value = 53.123;

        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}