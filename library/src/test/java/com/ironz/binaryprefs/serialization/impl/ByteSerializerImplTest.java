package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.serialization.Serializer;
import org.junit.Test;

import static org.junit.Assert.*;

public class ByteSerializerImplTest {

    private static final byte INCORRECT_FLAG = 0;

    private final Serializer<Byte> serializer = new ByteSerializerImpl();

    @Test
    public void byteConvert() {
        byte value = 53;

        byte[] bytes = serializer.serialize(value);
        byte restored = serializer.deserialize(Serializer.EMPTY_KEY, bytes);

        assertTrue(serializer.isMatches(((Object) value)));
        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);
        assertEquals(value, restored);
    }

    @Test
    public void byteIncorrectFlag() {
        byte value = 53;
        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}