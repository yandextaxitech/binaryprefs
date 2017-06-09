package com.ironz.binaryprefs.serialization.impl;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class StringSetSerializerImplTest {

    private static final byte INCORRECT_FLAG = 0;

    private final StringSetSerializerImpl serializer = new StringSetSerializerImpl();

    @Test
    public void emptyStringSet() {
        Set<String> value = new HashSet<>();

        byte[] bytes = serializer.serialize(value);
        Set<String> restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(value));
        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(serializer.bytesLength(), bytes.length);
        assertEquals(value, restored);
    }

    @Test
    public void stringSetConvert() {
        Set<String> value = new HashSet<>();
        value.add("One");
        value.add("Two");
        value.add("Three");
        value.add("");

        byte[] bytes = serializer.serialize(value);
        Set<String> restored = serializer.deserialize(bytes);

        assertTrue(serializer.isMatches(value));
        assertTrue(serializer.isMatches(bytes[0]));
        assertEquals(28, bytes.length);
        assertEquals(value, restored);
    }

    @Test
    public void stringSetIncorrectFlag() {
        Set<String> value = Collections.emptySet();

        byte[] bytes = serializer.serialize(value);
        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }
}