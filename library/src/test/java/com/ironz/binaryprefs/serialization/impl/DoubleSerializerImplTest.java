package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;
import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleSerializerImplTest {

    private static final byte INCORRECT_FLAG = 0;

    private final Serializer<Double> serializer = new DoubleSerializerImpl();

    @Test
    public void doubleConvert() {
        double value = 53.123;

        byte[] bytes = serializer.serialize(value);
        double restored = serializer.deserialize(Serializer.EMPTY_KEY, bytes);

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