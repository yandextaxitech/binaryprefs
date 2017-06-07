package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.impl.TestMigrateUser;
import com.ironz.binaryprefs.impl.TestUser;
import com.ironz.binaryprefs.serialization.persistable.Persistable;
import org.junit.Test;

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

        assertEquals(Persistable.SIZE_STRING, bytes.length);
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

        assertEquals(Persistable.SIZE_STRING + value.getBytes().length, bytes.length);
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
        int value = 53;
        byte[] bytes = Bits.intToBytesWithFlag(value);

        int restored = Bits.intFromBytesWithFlag(bytes);

        assertEquals(Persistable.SIZE_INT, bytes.length);
        assertEquals(Bits.FLAG_INT, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void integerIncorrectFlag() {
        int value = 53;
        byte[] bytes = Bits.intToBytesWithFlag(value);

        bytes[0] = INCORRECT_FLAG;

        Bits.intFromBytesWithFlag(bytes);
    }

    @Test
    public void longConvert() {
        long value = 53L;
        byte[] bytes = Bits.longToBytesWithFlag(value);

        long restored = Bits.longFromBytesWithFlag(bytes);

        assertEquals(Persistable.SIZE_LONG, bytes.length);
        assertEquals(Bits.FLAG_LONG, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void longIncorrectFlag() {
        long value = 53L;
        byte[] bytes = Bits.longToBytesWithFlag(value);

        bytes[0] = INCORRECT_FLAG;

        Bits.longFromBytesWithFlag(bytes);
    }

    @Test
    public void doubleConvert() {
        double value = 53.123;
        byte[] bytes = Bits.doubleToBytesWithFlag(value);

        double restored = Bits.doubleFromBytesWithFlag(bytes);

        assertEquals(Persistable.SIZE_DOUBLE, bytes.length);
        assertEquals(Bits.FLAG_DOUBLE, bytes[0]);
        assertEquals(value, restored, .0);
    }

    @Test(expected = ClassCastException.class)
    public void doubleIncorrectFlag() {
        double value = 53.123;
        byte[] bytes = Bits.doubleToBytesWithFlag(value);

        bytes[0] = INCORRECT_FLAG;

        Bits.doubleFromBytesWithFlag(bytes);
    }

    @Test
    public void floatConvert() {
        float value = 53.123f;
        byte[] bytes = Bits.floatToBytesWithFlag(value);

        float restored = Bits.floatFromBytesWithFlag(bytes);

        assertEquals(Persistable.SIZE_FLOAT, bytes.length);
        assertEquals(Bits.FLAG_FLOAT, bytes[0]);
        assertEquals(value, restored, .0);
    }

    @Test(expected = ClassCastException.class)
    public void floatIncorrectFlag() {
        float value = 53.123f;
        byte[] bytes = Bits.floatToBytesWithFlag(value);

        bytes[0] = INCORRECT_FLAG;

        Bits.floatFromBytesWithFlag(bytes);
    }

    @Test
    public void booleanConvert() {
        byte[] bytes = Bits.booleanToBytesWithFlag(true);

        boolean restored = Bits.booleanFromBytesWithFlag(bytes);

        assertEquals(Persistable.SIZE_BOOLEAN, bytes.length);
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
        byte value = 53;
        byte[] bytes = Bits.byteToBytesWithFlag(value);

        byte restored = Bits.byteFromBytesWithFlag(bytes);

        assertEquals(Persistable.SIZE_BYTE, bytes.length);
        assertEquals(Bits.FLAG_BYTE, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void byteIncorrectFlag() {
        byte value = 53;
        byte[] bytes = Bits.byteToBytesWithFlag(value);

        bytes[0] = INCORRECT_FLAG;

        Bits.byteFromBytesWithFlag(bytes);
    }

    @Test
    public void shortConvert() {
        short value = 53;
        byte[] bytes = Bits.shortToBytesWithFlag(value);

        short restored = Bits.shortFromBytesWithFlag(bytes);

        assertEquals(Persistable.SIZE_SHORT, bytes.length);
        assertEquals(Bits.FLAG_SHORT, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void shortIncorrectFlag() {
        short value = 53;
        byte[] bytes = Bits.shortToBytesWithFlag(value);

        bytes[0] = INCORRECT_FLAG;

        Bits.shortFromBytesWithFlag(bytes);
    }

    @Test
    public void charConvert() {
        byte[] bytes = Bits.charToBytesWithFlag(Character.MAX_VALUE);

        char restored = Bits.charFromBytesWithFlag(bytes);

        assertEquals(Persistable.SIZE_CHAR, bytes.length);
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
    public void persistableConvert() {
        TestUser value = TestUser.createUser();

        byte[] bytes = Bits.persistableToBytes(value);

        TestUser restored = Bits.persistableFromBytes(bytes, TestUser.class);

        assertEquals(Persistable.FLAG_PERSISTABLE, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void persistableIncorrectFlag() {
        TestUser value = TestUser.createUser();

        byte[] bytes = Bits.persistableToBytes(value);

        bytes[0] = INCORRECT_FLAG;
        Bits.persistableFromBytes(bytes, TestUser.class);
    }

    @Test(expected = ClassCastException.class)
    public void persistableIncorrectReadConvert() {
        TestUser value = TestUser.createUser();

        byte[] bytes = Bits.persistableToBytes(value);

        Bits.persistableFromBytes(bytes, TestMigrateUser.class);
    }

    @Test(expected = ClassCastException.class)
    public void persistableIncorrectReadBackConvert() {
        TestMigrateUser value = TestMigrateUser.createUser();

        byte[] bytes = Bits.persistableToBytes(value);

        Bits.persistableFromBytes(bytes, TestUser.class);
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
        float value = 1.78f;

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

    @Test(expected = UnsupportedClassVersionError.class)
    public void trySerializeUnsupported() {
        Object o = new Object();
        Bits.trySerializeByType(o);
    }

    @Test
    public void trySerializeStringSet() {
        Set<String> value = Collections.singleton("Some string");

        byte[] bytes = Bits.trySerializeByType(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(HashSet.class, o.getClass());
    }

    @Test
    public void trySerializeString() {
        String value = "Some string";

        byte[] bytes = Bits.trySerializeByType(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(String.class, o.getClass());
    }

    @Test
    public void trySerializeInt() {
        int value = Integer.MAX_VALUE;

        byte[] bytes = Bits.trySerializeByType(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(Integer.class, o.getClass());
    }

    @Test
    public void trySerializeLong() {
        long value = Long.MAX_VALUE;

        byte[] bytes = Bits.trySerializeByType(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(Long.class, o.getClass());
    }

    @Test
    public void trySerializeFloat() {
        float value = 1.78f;

        byte[] bytes = Bits.trySerializeByType(value);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(Float.class, o.getClass());
    }

    @Test
    public void trySerializeBoolean() {

        byte[] bytes = Bits.trySerializeByType(true);
        Object o = Bits.tryDeserializeByFlag(bytes);

        assertEquals(Boolean.class, o.getClass());
    }

}