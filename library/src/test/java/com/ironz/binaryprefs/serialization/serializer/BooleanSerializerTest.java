package com.ironz.binaryprefs.serialization.serializer;

import org.junit.Test;

import static org.junit.Assert.*;

public final class BooleanSerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private final BooleanSerializer serializer = new BooleanSerializer();

    @Test
    public void booleanConvert() {
        byte[] bytes = serializer.serialize(true);

        boolean restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);
        assertEquals(true, restored);
    }

    @Test
    public void booleanIncorrectFlag() {
        byte[] bytes = serializer.serialize(true);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}