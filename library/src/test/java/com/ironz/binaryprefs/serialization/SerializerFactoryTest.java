package com.ironz.binaryprefs.serialization;

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
        byte[] bytes = factory.serialize(true);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(true, o);
    }

    @Test
    public void byteSerializerType() {
        byte value = Byte.MAX_VALUE;
        byte[] bytes = factory.serialize(value);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(value, o);
    }

    @Test
    public void charSerializerType() {
        char value = Character.MAX_VALUE;
        byte[] bytes = factory.serialize(value);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(value, o);
    }

    @Test
    public void doubleSerializerType() {
        double value = Double.MAX_VALUE;
        byte[] bytes = factory.serialize(value);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(value, o);
    }

    @Test
    public void floatSerializerType() {
        float value = Float.MAX_VALUE;
        byte[] bytes = factory.serialize(value);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(value, o);
    }

    @Test
    public void integerSerializerType() {
        int value = Integer.MAX_VALUE;
        byte[] bytes = factory.serialize(value);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(value, o);
    }

    @Test
    public void longSerializerType() {
        long value = Long.MAX_VALUE;
        byte[] bytes = factory.serialize(value);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(value, o);
    }

    @Test
    public void shortSerializerType() {
        short value = Short.MAX_VALUE;
        byte[] bytes = factory.serialize(value);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(value, o);
    }

    @Test
    public void stringSerializerType() {
        String value = String.class.getName();
        byte[] bytes = factory.serialize(value);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(value, o);
    }

    @Test
    public void stringSetSerializerType() {
        HashSet<Object> value = new HashSet<>();
        byte[] bytes = factory.serialize(value);

        Object o = factory.deserialize(SerializerFactory.EMPTY_TOKEN, bytes);

        assertEquals(value, o);
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void unknownSerializerType() {
        factory.serialize(new Object());
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void unknownSerializerFlag() {
        factory.deserialize(SerializerFactory.EMPTY_TOKEN, new byte[]{INCORRECT_FLAG});
    }
}