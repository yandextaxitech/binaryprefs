package com.ironz.binaryprefs.serialization.serializer.persistable;

import com.ironz.binaryprefs.impl.TestUser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersistableRegistryTest {

    private PersistableRegistry registry;

    @Before
    public void setUp() {
        registry = new PersistableRegistry();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void registerTwice() {
        registry.register(TestUser.KEY, TestUser.class);
        registry.register(TestUser.KEY, TestUser.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void registerTwiceDifferentTypes() {
        registry.register(TestUser.KEY, TestUser.class);
        registry.register(TestUser.KEY, Persistable.class);
    }

    @Test
    public void register() {
        String key = TestUser.KEY;
        Class<TestUser> clazz = TestUser.class;

        registry.register(key, clazz);
        Class<? extends Persistable> getType = registry.get(key);

        assertEquals(clazz, getType);
    }

    @Test
    public void registerTypeWithTwoKeys() {
        String key = TestUser.KEY;
        String key2 = key + "2";
        Class<TestUser> type = TestUser.class;

        registry.register(key, type);
        registry.register(key2, type);

        Class<? extends Persistable> getType = registry.get(key);
        Class<? extends Persistable> getType2 = registry.get(key2);

        assertEquals(type, getType);
        assertEquals(type, getType2);
    }
}