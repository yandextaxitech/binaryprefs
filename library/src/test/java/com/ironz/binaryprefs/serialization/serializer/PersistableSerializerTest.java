package com.ironz.binaryprefs.serialization.serializer;

import com.ironz.binaryprefs.impl.TestMigrateUser;
import com.ironz.binaryprefs.impl.TestUser;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PersistableSerializerTest {

    private static final byte INCORRECT_FLAG = 0;

    private PersistableSerializer serializer;

    @Before
    public void setUp() {
        PersistableRegistry persistableRegistry = new PersistableRegistry();
        persistableRegistry.register(TestUser.KEY, TestUser.class);
        persistableRegistry.register(TestMigrateUser.KEY, TestMigrateUser.class);
        SerializerFactory factory = new SerializerFactory(persistableRegistry);
        serializer = factory.getPersistableSerializer();
    }

    @Test
    public void convert() {
        TestUser value = TestUser.create();

        byte[] bytes = serializer.serialize(value);

        TestUser restored = (TestUser) serializer.deserialize(TestUser.KEY, bytes);

        assertEquals(value, restored);
    }

    @Test
    public void incorrectFlag() {
        TestUser value = TestUser.create();

        byte[] bytes = serializer.serialize(value);

        bytes[0] = INCORRECT_FLAG;

        assertFalse(serializer.isMatches(bytes[0]));
    }

    @Test(expected = ClassCastException.class)
    public void incorrectReadConvert() {
        TestUser value = TestUser.create();

        byte[] bytes = serializer.serialize(value);

        serializer.deserialize(TestMigrateUser.KEY, bytes);
    }

    @Test(expected = ClassCastException.class)
    public void incorrectReadBackConvert() {
        TestMigrateUser value = TestMigrateUser.create();

        byte[] bytes = serializer.serialize(value);

        serializer.deserialize(TestUser.KEY, bytes);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void outOfBounds() {
        TestUser value = TestUser.create();

        byte[] bytes = serializer.serialize(value);

        int trimmedLength = bytes.length - 1;
        byte[] trimmed = new byte[trimmedLength];
        System.arraycopy(bytes, 0, trimmed, 0, trimmedLength);

        serializer.deserialize(TestUser.KEY, trimmed);
    }
}