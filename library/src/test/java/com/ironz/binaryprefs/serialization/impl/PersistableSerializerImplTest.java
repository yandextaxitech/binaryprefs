package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.impl.TestMigrateUser;
import com.ironz.binaryprefs.impl.TestUser;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.impl.persistable.Persistable;
import com.ironz.binaryprefs.serialization.impl.persistable.PersistableRegistry;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersistableSerializerImplTest {

    private static final byte INCORRECT_FLAG = 0;

    private PersistableSerializerImpl serializer;
    private PersistableRegistry persistableRegistry;

    @Before
    public void setUp() {
        persistableRegistry = new PersistableRegistry();
        persistableRegistry.register(TestUser.KEY, TestUser.class);
        persistableRegistry.register(TestMigrateUser.KEY, TestMigrateUser.class);
        SerializerFactory factory = new SerializerFactory(persistableRegistry);
        serializer = factory.getPersistableSerializer();
    }

    @Test
    public void persistableConvert() {
        TestUser value = TestUser.create();

        byte[] bytes = serializer.serialize(value);

        TestUser restored = (TestUser) serializer.deserialize(TestUser.KEY, bytes);

        assertEquals(Persistable.FLAG_PERSISTABLE, bytes[0]);
        assertEquals(value, restored);
    }

    @Test(expected = ClassCastException.class)
    public void persistableIncorrectFlag() {
        TestUser value = TestUser.create();

        byte[] bytes = serializer.serialize(value);

        bytes[0] = INCORRECT_FLAG;
        serializer.deserialize(TestUser.KEY, bytes);
    }

    @Test(expected = ClassCastException.class)
    public void persistableIncorrectReadConvert() {
        TestUser value = TestUser.create();

        byte[] bytes = serializer.serialize(value);

        serializer.deserialize(TestMigrateUser.KEY, bytes);
    }

    @Test(expected = ClassCastException.class)
    public void persistableIncorrectReadBackConvert() {
        TestMigrateUser value = TestMigrateUser.create();

        byte[] bytes = serializer.serialize(value);

        serializer.deserialize(TestUser.KEY, bytes);
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void persistableIncorrectRegistryToken() {
        TestMigrateUser value = TestMigrateUser.create();

        byte[] bytes = serializer.serialize(value);

        persistableRegistry.remove(TestUser.KEY);

        serializer.deserialize(TestUser.KEY, bytes);
    }
}