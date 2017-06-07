package com.ironz.binaryprefs.serialization;

import org.junit.Test;

import static org.junit.Assert.*;

public class LongSerializerImplTest {

    private static final byte INCORRECT_FLAG = 0;

    private final Serializer<Long> serializer = new LongSerializerImpl();

    @Test
    public void longConvert() {
        long value = 53L;
        byte[] bytes = serializer.serialize(value);

        long restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(value));
        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);
        assertEquals(value, restored);
    }

    @Test
    public void longIncorrectFlag() {
        long value = 53L;

        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}