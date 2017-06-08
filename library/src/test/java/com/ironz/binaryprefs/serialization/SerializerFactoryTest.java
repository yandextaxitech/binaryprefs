package com.ironz.binaryprefs.serialization;

import com.ironz.binaryprefs.serialization.impl.*;
import com.ironz.binaryprefs.serialization.impl.persistable.PersistableRegistry;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class SerializerFactoryTest {

    private final PersistableRegistry classProvider = new PersistableRegistry();
    private final SerializerFactory factory = new SerializerFactory(classProvider);

    @Test
    public void booleanSerializerType() {
        Serializer serializer = factory.getByClassType(true);
        assertEquals(BooleanSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void byteSerializerType() {
        Serializer serializer = factory.getByClassType(Byte.MAX_VALUE);
        assertEquals(ByteSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void charSerializerType() {
        Serializer serializer = factory.getByClassType(Character.MAX_VALUE);
        assertEquals(CharSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void doubleSerializerType() {
        Serializer serializer = factory.getByClassType(Double.MAX_VALUE);
        assertEquals(DoubleSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void floatSerializerType() {
        Serializer serializer = factory.getByClassType(Float.MAX_VALUE);
        assertEquals(FloatSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void integerSerializerType() {
        Serializer serializer = factory.getByClassType(Integer.MAX_VALUE);
        assertEquals(IntegerSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void longSerializerType() {
        Serializer serializer = factory.getByClassType(Long.MAX_VALUE);
        assertEquals(LongSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void shortSerializerType() {
        Serializer serializer = factory.getByClassType(Short.MAX_VALUE);
        assertEquals(ShortSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSerializerType() {
        Serializer serializer = factory.getByClassType(String.class.getName());
        assertEquals(StringSerializerImpl.class, serializer.getClass());
    }

    @Test
    public void stringSetSerializerType() {
        Serializer serializer = factory.getByClassType(new HashSet<>());
        assertEquals(StringSetSerializerImpl.class, serializer.getClass());
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void unknownSerializerType() {
        factory.getByClassType(new Object());
    }
}