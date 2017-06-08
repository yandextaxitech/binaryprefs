package com.ironz.binaryprefs.serialization.impl.persistable;

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
        registry.register(TestUser.KEY, TestUser.class);
        Class<? extends Persistable> clazz = registry.get(TestUser.KEY);
        assertEquals(TestUser.class, clazz);
    }

    @Test(expected = UnsupportedClassVersionError.class)
    public void remove() {
        registry.register(TestUser.KEY, TestUser.class);
        registry.remove(TestUser.KEY);
        registry.get(TestUser.KEY);
    }
}