package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.impl.*;
import com.ironz.binaryprefs.serialization.impl.persistable.PersistableRegistry;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class SerializerFactoryTest {

    private static final byte INCORRECT_FLAG = 0;

    private final PersistableRegistry classProvider = new PersistableRegistry();
    private final SerializerFactory factory = new SerializerFactory(classProvider);

    @Test
    public void booleanSerializerType() {
        Serializer serializer = factory.getByClassType(true);
        assertEquals(BooleanSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void booleanSerializerFlag() {
        Serializer serializer = factory.getByFlag(BooleanSerializerImpl.BOOLEAN_FLAG);
        assertEquals(BooleanSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void byteSerializerType() {
        Serializer serializer = factory.getByClassType(Byte.MAX_VALUE);
        assertEquals(ByteSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void byteSerializerFlag() {
        Serializer serializer = factory.getByFlag(ByteSerializerImpl.BYTE_FLAG);
        assertEquals(ByteSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void charSerializerType() {
        Serializer serializer = factory.getByClassType(Character.MAX_VALUE);
        assertEquals(CharSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void charSerializerFlag() {
        Serializer serializer = factory.getByFlag(CharSerializerImpl.CHAR_FLAG);
        assertEquals(CharSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void doubleSerializerType() {
        Serializer serializer = factory.getByClassType(Double.MAX_VALUE);
        assertEquals(DoubleSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void doubleSerializerFlag() {
        Serializer serializer = factory.getByFlag(DoubleSerializerImpl.DOUBLE_FLAG);
        assertEquals(DoubleSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void floatSerializerType() {
        Serializer serializer = factory.getByClassType(Float.MAX_VALUE);
        assertEquals(FloatSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void floatSerializerFlag() {
        Serializer serializer = factory.getByFlag(FloatSerializerImpl.FLOAT_FLAG);
        assertEquals(FloatSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void integerSerializerType() {
        Serializer serializer = factory.getByClassType(Integer.MAX_VALUE);
        assertEquals(IntegerSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void integerSerializerFlag() {
        Serializer serializer = factory.getByFlag(IntegerSerializerImpl.INT_FLAG);
        assertEquals(IntegerSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void longSerializerType() {
        Serializer serializer = factory.getByClassType(Long.MAX_VALUE);
        assertEquals(LongSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void longSerializerFlag() {
        Serializer serializer = factory.getByFlag(LongSerializerImpl.LONG_FLAG);
        assertEquals(LongSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void shortSerializerType() {
        Serializer serializer = factory.getByClassType(Short.MAX_VALUE);
        assertEquals(ShortSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void shortSerializerFlag() {
        Serializer serializer = factory.getByFlag(ShortSerializerImpl.SHORT_FLAG);
        assertEquals(ShortSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSerializerType() {
        Serializer serializer = factory.getByClassType(String.class.getName());
        assertEquals(StringSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSerializerFlag() {
        Serializer serializer = factory.getByFlag(StringSerializerImpl.STRING_FLAG);
        assertEquals(StringSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSetSerializerType() {
        Serializer serializer = factory.getByClassType(new HashSet<>());
        assertEquals(StringSetSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSetSerializerFlag() {
        Serializer serializer = factory.getByFlag(StringSetSerializerImpl.STRING_SET_FLAG);
        assertEquals(StringSetSerializerImpl.class, serializer.getClass());
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void unknownSerializerType() {
        factory.getByClassType(new Object());
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void unknownSerializerFlag() {
        factory.getByFlag(INCORRECT_FLAG);
    }
}