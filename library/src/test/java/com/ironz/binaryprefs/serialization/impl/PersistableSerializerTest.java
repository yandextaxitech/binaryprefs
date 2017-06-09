package com.ironz.binaryprefs.serialization.impl;

import com.ironz.binaryprefs.impl.TestMigrateUser;
import com.ironz.binaryprefs.impl.TestUser;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.impl.persistable.PersistableRegistry;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PersistableSerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private PersistableSerializer serializer;
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

        assertEquals(value, restored);
    }

    @Test
    public void persistableIncorrectFlag() {
        TestUser value = TestUser.create();

        byte[] bytes = serializer.serialize(value);

        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
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