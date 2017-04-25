package com.ironz.binaryprefs.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitsTest {

    @Test
    public void integerConvert() {
        byte[] bytes = Bits.intToBytes(Integer.MAX_VALUE);
        int i = Bits.intFromBytes(bytes);
        assertEquals(Integer.MAX_VALUE, i);
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
        float f = Bits.floatFromBytes(bytes);
        assertEquals(Float.MAX_VALUE, f, .0);
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
        long l = Bits.longFromBytes(bytes);
        assertEquals(Long.MAX_VALUE, l);
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
        boolean b = Bits.booleanFromBytes(bytes);
        assertEquals(true, b);
    }

    @Test(expected = ClassCastException.class)
    public void booleanIncorrectFlag() {
        byte[] bytes = Bits.booleanToBytes(true);
        bytes[0] = 0;
        Bits.booleanFromBytes(bytes);
    }
}