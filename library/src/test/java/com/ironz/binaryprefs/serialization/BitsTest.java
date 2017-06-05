package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.impl.TestAddress;
import com.ironz.binaryprefs.impl.TestUser;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class BitsTest {

    private static final byte INCORRECT_FLAG = 0;

    @Test
    public void emptyStringSet() {
        Set<String> value = new HashSet<>();

        byte[] bytes = Bits.stringSetToBytesWithFlag(value);
        Set<String> restored = Bits.stringSetFromBytesWithFlag(bytes);

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

        byte[] bytes = Bits.stringSetToBytesWithFlag(value);
        Set<String> restored = Bits.stringSetFromBytesWithFlag(bytes);

        assertEquals(32, bytes.length);
        assertEquals(Bits.FLAG_STRING_SET, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void stringSetIncorrectFlag() {
        Set<String> value = Collections.emptySet();

        byte[] bytes = Bits.stringSetToBytesWithFlag(value);

        bytes[0] = INCORRECT_FLAG;

        Bits.stringSetFromBytesWithFlag(bytes);
    }

    @Test
    public void stringConvert() {
        String value = "Some String";

        byte[] bytes = Bits.stringToBytesWithFlag(value);
        String restored = Bits.stringFromBytesWithFlag(bytes);

        assertEquals(12, bytes.length);
        assertEquals(Bits.FLAG_STRING, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void stringIncorrectFlag() {
        String value = "Some String";

        byte[] bytes = Bits.stringToBytesWithFlag(value);
        bytes[0] = INCORRECT_FLAG;

        Bits.stringFromBytesWithFlag(bytes);
    }

    @Test
    public void integerConvert() {
        byte[] bytes = Bits.intToBytesWithFlag(Integer.MAX_VALUE);

        int restored = Bits.intFromBytesWithFlag(bytes);

        assertEquals(5, bytes.length);
        assertEquals(Bits.FLAG_INT, bytes[0]);
        assertEquals(Integer.MAX_VALUE, restored);
    }

    @Test(expected = ClassCastException.class)
    public void integerIncorrectFlag() {
        byte[] bytes = Bits.intToBytesWithFlag(Integer.MAX_VALUE);

        bytes[0] = INCORRECT_FLAG;

        Bits.intFromBytesWithFlag(bytes);
    }

    @Test
    public void longConvert() {
        byte[] bytes = Bits.longToBytesWithFlag(Long.MAX_VALUE);

        long restored = Bits.longFromBytesWithFlag(bytes);

        assertEquals(9, bytes.length);
        assertEquals(Bits.FLAG_LONG, bytes[0]);
        assertEquals(Long.MAX_VALUE, restored);
    }

    @Test(expected = ClassCastException.class)
    public void longIncorrectFlag() {
        byte[] bytes = Bits.longToBytesWithFlag(Long.MAX_VALUE);

        bytes[0] = INCORRECT_FLAG;

        Bits.longFromBytesWithFlag(bytes);
    }

    @Test
    public void doubleConvert() {
        double value = 53.123;
        byte[] bytes = Bits.doubleToBytesWithFlag(value);

        double restored = Bits.doubleFromBytesWithFlag(bytes);

        assertEquals(9, bytes.length);
        assertEquals(Bits.FLAG_DOUBLE, bytes[0]);
        assertEquals(value, restored, .0);
    }

    @Test(expected = ClassCastException.class)
    public void doubleIncorrectFlag() {
        byte[] bytes = Bits.doubleToBytesWithFlag(Double.MAX_VALUE);

        bytes[0] = INCORRECT_FLAG;

        Bits.doubleFromBytesWithFlag(bytes);
    }

    @Test
    public void floatConvert() {
        float value = 1.78f;
        byte[] bytes = Bits.floatToBytesWithFlag(value);

        float restored = Bits.floatFromBytesWithFlag(bytes);

        assertEquals(5, bytes.length);
        assertEquals(Bits.FLAG_FLOAT, bytes[0]);
        assertEquals(value, restored, .0);
    }

    @Test(expected = ClassCastException.class)
    public void floatIncorrectFlag() {
        byte[] bytes = Bits.floatToBytesWithFlag(Float.MAX_VALUE);

        bytes[0] = INCORRECT_FLAG;

        Bits.floatFromBytesWithFlag(bytes);
    }

    @Test
    public void booleanConvert() {
        byte[] bytes = Bits.booleanToBytesWithFlag(true);

        boolean restored = Bits.booleanFromBytesWithFlag(bytes);

        assertEquals(2, bytes.length);
        assertEquals(Bits.FLAG_BOOLEAN, bytes[0]);
        assertEquals(true, restored);
    }

    @Test(expected = ClassCastException.class)
    public void booleanIncorrectFlag() {
        byte[] bytes = Bits.booleanToBytesWithFlag(true);

        bytes[0] = INCORRECT_FLAG;

        Bits.booleanFromBytesWithFlag(bytes);
    }

    @Test
    public void byteConvert() {
        byte[] bytes = Bits.byteToBytesWithFlag(Byte.MAX_VALUE);

        byte restored = Bits.byteFromBytesWithFlag(bytes);

        assertEquals(2, bytes.length);
        assertEquals(Bits.FLAG_BYTE, bytes[0]);
        assertEquals(Byte.MAX_VALUE, restored);
    }

    @Test(expected = ClassCastException.class)
    public void byteIncorrectFlag() {
        byte[] bytes = Bits.byteToBytesWithFlag(Byte.MAX_VALUE);

        bytes[0] = INCORRECT_FLAG;

        Bits.byteFromBytesWithFlag(bytes);
    }

    @Test
    public void shortConvert() {
        byte[] bytes = Bits.shortToBytesWithFlag(Short.MAX_VALUE);

        short restored = Bits.shortFromBytesWithFlag(bytes);

        assertEquals(3, bytes.length);
        assertEquals(Bits.FLAG_SHORT, bytes[0]);
        assertEquals(Short.MAX_VALUE, restored);
    }

    @Test(expected = ClassCastException.class)
    public void shortIncorrectFlag() {
        byte[] bytes = Bits.shortToBytesWithFlag(Short.MAX_VALUE);

        bytes[0] = INCORRECT_FLAG;

        Bits.shortFromBytesWithFlag(bytes);
    }

    @Test
    public void charConvert() {
        byte[] bytes = Bits.charToBytesWithFlag(Character.MAX_VALUE);

        char restored = Bits.charFromBytesWithFlag(bytes);

        assertEquals(3, bytes.length);
        assertEquals(Bits.FLAG_CHAR, bytes[0]);
        assertEquals(Character.MAX_VALUE, restored);
    }

    @Test(expected = ClassCastException.class)
    public void charIncorrectFlag() {
        byte[] bytes = Bits.charToBytesWithFlag(Character.MAX_VALUE);

        bytes[0] = INCORRECT_FLAG;

        Bits.charFromBytesWithFlag(bytes);
    }

    @Test
    public void dataObjectStreams() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeDouble(53.123);
        objectOutputStream.writeDouble(35.098);

        outputStream.flush();
        objectOutputStream.flush();

        byte[] bytes = outputStream.toByteArray();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream stream = new ObjectInputStream(inputStream);
        double v = stream.readDouble();
        double v1 = stream.readDouble();
        System.out.println("v: " + v + ", v1: " + v1);
    }

    @Test
    public void persistableConvert() {
        TestUser value = new TestUser();
        value.setName("John");
        value.setAge((short) 21);
        value.setSex('M');
        value.setMarried(true);
        value.setPostal(1234567890L);
        value.setChild((byte) 19);
        value.setWeight(74.2f);
        value.setHeight(1.78f);
        value.addAddresses(new TestAddress("USA", "New York", "1th", 25, 53.123, 35.098));
        value.addAddresses(new TestAddress("Russia", "Moscow", "Red Square", 1, 53.123, 35.098));

        byte[] bytes = Bits.persistableToBytes(value);

        TestUser restored = Bits.persistableFromBytes(bytes, TestUser.class);

        assertEquals(Persistable.FLAG_PERSISTABLE, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void tryDeserializeUnsupported() {
        byte[] bytes = {0};
        Bits.tryDeserializeByFlag(bytes);
    }

    @Test
    public void tryDeserializeStringSet() {
        Set<String> value = Collections.singleton("Some string");

        byte[] bytes = Bits.stringSetToBytesWithFlag(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(HashSet.class, o.getClass());
    }

    @Test
    public void tryDeserializeString() {
        String value = "Some string";

        byte[] bytes = Bits.stringToBytesWithFlag(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(String.class, o.getClass());
    }

    @Test
    public void tryDeserializeInt() {
        int value = Integer.MAX_VALUE;

        byte[] bytes = Bits.intToBytesWithFlag(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(Integer.class, o.getClass());
    }

    @Test
    public void tryDeserializeLong() {
        long value = Long.MAX_VALUE;

        byte[] bytes = Bits.longToBytesWithFlag(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(Long.class, o.getClass());
    }

    @Test
    public void tryDeserializeFloat() {
        float value = Float.MAX_VALUE;

        byte[] bytes = Bits.floatToBytesWithFlag(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(Float.class, o.getClass());
    }

    @Test
    public void tryDeserializeBoolean() {

        byte[] bytes = Bits.booleanToBytesWithFlag(true);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(Boolean.class, o.getClass());
    }
}