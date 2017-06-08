package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;
import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanSerializerImplTest {

    private static final byte INCORRECT_FLAG = 0;

    private final Serializer<Boolean> serializer = new BooleanSerializerImpl();

    @Test
    public void booleanConvert() {
        byte[] bytes = serializer.serialize(true);

        boolean restored = serializer.deserialize(Serializer.EMPTY_KEY, bytes);

        assertTrue(serializer.isMatches(true));
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