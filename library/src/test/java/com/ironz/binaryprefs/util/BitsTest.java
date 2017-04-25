package com.ironz.binaryprefs.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitsTest {

    @Test
    public void stringConvert() {
        String someString = "Some String";

        byte[] bytes = Bits.stringToBytes(someString);
        String restored = Bits.stringFromBytes(bytes);

        assertEquals(someString, restored);
    }

    @Test(expected = ClassCastException.class)
    public void StringIncorrectFlag() {
        byte[] bytes = Bits.intToBytes(Integer.MAX_VALUE);

        bytes[0] = 0;

        Bits.intFromBytes(bytes);
    }

    @Test
    public void integerConvert() {
        byte[] bytes = Bits.intToBytes(Integer.MAX_VALUE);

        int restored = Bits.intFromBytes(bytes);

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

        assertEquals(true, restored);
    }

    @Test(expected = ClassCastException.class)
    public void booleanIncorrectFlag() {
        byte[] bytes = Bits.booleanToBytes(true);

        bytes[0] = 0;

        Bits.booleanFromBytes(bytes);
    }
}