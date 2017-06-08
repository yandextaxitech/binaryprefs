package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.impl.*;
import com.ironz.binaryprefs.serialization.impl.persistable.PersistableRegistry;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class SerializerFactoryTest {

    private static final byte INCORRECT_FLAG = 0;

    private final PersistableRegistry registry = new PersistableRegistry();
    private final SerializerFactory factory = new SerializerFactory(registry);

    @Test
    public void booleanSerializerType() {
        Serializer serializer = factory.serialize(true);
        assertEquals(BooleanSerializer.class, serializer.getClass());
    }

    @Test
    public void booleanSerializerFlag() {
        Serializer serializer = factory.deserialize(key, BooleanSerializer.BOOLEAN_FLAG);
        assertEquals(BooleanSerializer.class, serializer.getClass());
    }

    @Test
    public void byteSerializerType() {
        Serializer serializer = factory.serialize(Byte.MAX_VALUE);
        assertEquals(ByteSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void byteSerializerFlag() {
        Serializer serializer = factory.deserialize(key, ByteSerializerImpl.BYTE_FLAG);
        assertEquals(ByteSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void charSerializerType() {
        Serializer serializer = factory.serialize(Character.MAX_VALUE);
        assertEquals(CharSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void charSerializerFlag() {
        Serializer serializer = factory.deserialize(key, CharSerializerImpl.CHAR_FLAG);
        assertEquals(CharSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void doubleSerializerType() {
        Serializer serializer = factory.serialize(Double.MAX_VALUE);
        assertEquals(DoubleSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void doubleSerializerFlag() {
        Serializer serializer = factory.deserialize(key, DoubleSerializerImpl.DOUBLE_FLAG);
        assertEquals(DoubleSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void floatSerializerType() {
        Serializer serializer = factory.serialize(Float.MAX_VALUE);
        assertEquals(FloatSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void floatSerializerFlag() {
        Serializer serializer = factory.deserialize(key, FloatSerializerImpl.FLOAT_FLAG);
        assertEquals(FloatSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void integerSerializerType() {
        Serializer serializer = factory.serialize(Integer.MAX_VALUE);
        assertEquals(IntegerSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void integerSerializerFlag() {
        Serializer serializer = factory.deserialize(key, IntegerSerializerImpl.INT_FLAG);
        assertEquals(IntegerSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void longSerializerType() {
        Serializer serializer = factory.serialize(Long.MAX_VALUE);
        assertEquals(LongSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void longSerializerFlag() {
        Serializer serializer = factory.deserialize(key, LongSerializerImpl.LONG_FLAG);
        assertEquals(LongSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void shortSerializerType() {
        Serializer serializer = factory.serialize(Short.MAX_VALUE);
        assertEquals(ShortSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void shortSerializerFlag() {
        Serializer serializer = factory.deserialize(key, ShortSerializerImpl.SHORT_FLAG);
        assertEquals(ShortSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSerializerType() {
        Serializer serializer = factory.serialize(String.class.getName());
        assertEquals(StringSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSerializerFlag() {
        Serializer serializer = factory.deserialize(key, StringSerializerImpl.STRING_FLAG);
        assertEquals(StringSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSetSerializerType() {
        Serializer serializer = factory.serialize(new HashSet<>());
        assertEquals(StringSetSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSetSerializerFlag() {
        Serializer serializer = factory.deserialize(key, StringSetSerializerImpl.STRING_SET_FLAG);
        assertEquals(StringSetSerializerImpl.class, serializer.getClass());
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void unknownSerializerType() {
        factory.serialize(new Object());
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void unknownSerializerFlag() {
        factory.deserialize(key, INCORRECT_FLAG);
    }
}