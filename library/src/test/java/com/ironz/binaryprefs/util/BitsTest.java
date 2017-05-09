package com.ironz.binaryprefs.util;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class BitsTest {

    @Test
    public void emptyStringSet() {
        Set<String> value = new HashSet<>();

        byte[] bytes = Bits.stringSetToBytes(value);
        Set<String> restored = Bits.stringSetFromBytes(bytes);

        assertEquals(1, bytes.length);
        assertEquals(Bits.FLAG_STRING_SET, bytes[0]);
        assertEquals(value, restored);
    }

    @Test
    public void stringSetConvert() {
        Set<String> value = new HashSet<>();
        value.add("One");
        value.add("Two");
        value.add("Three");
        value.add("");
        value.add(null);

        byte[] bytes = Bits.stringSetToBytes(value);
        Set<String> restored = Bits.stringSetFromBytes(bytes);

        assertEquals(37, bytes.length);
        assertEquals(Bits.FLAG_STRING_SET, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void stringSetIncorrectFlag() {
        Set<String> value = new HashSet<>();
        value.add("One");
        value.add("Two");
        value.add("Three");
        value.add("");
        value.add(null);

        byte[] bytes = Bits.stringSetToBytes(value);
        bytes[0] = 0;
        Bits.stringSetFromBytes(bytes);
    }

    @Test
    public void stringConvert() {
        String value = "Some String";

        byte[] bytes = Bits.stringToBytes(value);
        String restored = Bits.stringFromBytes(bytes);

        assertEquals(12, bytes.length);
        assertEquals(Bits.FLAG_STRING, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void stringIncorrectFlag() {
        byte[] bytes = Bits.intToBytes(Integer.MAX_VALUE);

        bytes[0] = 0;

        Bits.intFromBytes(bytes);
    }

    @Test
    public void integerConvert() {
        byte[] bytes = Bits.intToBytes(Integer.MAX_VALUE);

        int restored = Bits.intFromBytes(bytes);

        assertEquals(5, bytes.length);
        assertEquals(Bits.FLAG_INT, bytes[0]);
        assertEquals(Integer.MAX_VALUE, restored);
    }

    @Test(expected = ClassCastException.class)
    public void integerIncorrectFlag() {
        byte[] bytes = Bits.intToBytes(Integer.MAX_VALUE);

        bytes[0] = 0;

        Bits.intFromBytes(bytes);
    }

    @Test
    public void floatConvert() {
        byte[] bytes = Bits.floatToBytes(Float.MAX_VALUE);

        float restored = Bits.floatFromBytes(bytes);

        assertEquals(5, bytes.length);
        assertEquals(Bits.FLAG_FLOAT, bytes[0]);
        assertEquals(Float.MAX_VALUE, restored, .0);
    }

    @Test(expected = ClassCastException.class)
    public void floatIncorrectFlag() {
        byte[] bytes = Bits.floatToBytes(Float.MAX_VALUE);

        bytes[0] = 0;

        Bits.floatFromBytes(bytes);
    }

    @Test
    public void longConvert() {
        byte[] bytes = Bits.longToBytes(Long.MAX_VALUE);

        long restored = Bits.longFromBytes(bytes);

        assertEquals(9, bytes.length);
        assertEquals(Bits.FLAG_LONG, bytes[0]);
        assertEquals(Long.MAX_VALUE, restored);
    }

    @Test(expected = ClassCastException.class)
    public void longIncorrectFlag() {
        byte[] bytes = Bits.longToBytes(Long.MAX_VALUE);

        bytes[0] = 0;

        Bits.longFromBytes(bytes);
    }

    @Test
    public void booleanConvert() {
        byte[] bytes = Bits.booleanToBytes(true);

        boolean restored = Bits.booleanFromBytes(bytes);

        assertEquals(2, bytes.length);
        assertEquals(Bits.FLAG_BOOLEAN, bytes[0]);
        assertEquals(true, restored);
    }

    @Test(expected = ClassCastException.class)
    public void booleanIncorrectFlag() {
        byte[] bytes = Bits.booleanToBytes(true);

        bytes[0] = 0;

        Bits.booleanFromBytes(bytes);
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void tryDeserializeUnsupported() {
        byte[] bytes = {0};
        Bits.tryDeserialize(bytes);
    }

    @Test
    public void tryDeserializeStringSet() {
        Set<String> value = Collections.singleton("Some string");

        byte[] bytes = Bits.stringSetToBytes(value);
        Object o = Bits.tryDeserialize(bytes);

        assertEquals(HashSet.class, o.getClass());
    }

    @Test
    public void tryDeserializeString() {
        String value = "Some string";

        byte[] bytes = Bits.stringToBytes(value);
        Object o = Bits.tryDeserialize(bytes);

        assertEquals(String.class, o.getClass());
    }

    @Test
    public void tryDeserializeInt() {
        int value = Integer.MAX_VALUE;

        byte[] bytes = Bits.intToBytes(value);
        Object o = Bits.tryDeserialize(bytes);

        assertEquals(Integer.class, o.getClass());
    }

    @Test
    public void tryDeserializeLong() {
        long value = Long.MAX_VALUE;

        byte[] bytes = Bits.longToBytes(value);
        Object o = Bits.tryDeserialize(bytes);

        assertEquals(Long.class, o.getClass());
    }

    @Test
    public void tryDeserializeFloat() {
        float value = Float.MAX_VALUE;

        byte[] bytes = Bits.floatToBytes(value);
        Object o = Bits.tryDeserialize(bytes);

        assertEquals(Float.class, o.getClass());
    }

    @Test
    public void tryDeserializeBoolean() {

        byte[] bytes = Bits.booleanToBytes(true);
        Object o = Bits.tryDeserialize(bytes);

        assertEquals(Boolean.class, o.getClass());
    }
}