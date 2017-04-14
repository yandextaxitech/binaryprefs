package com.ironz.binaryprefs.name;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KeyNameProviderTest {

    private final KeyNameProvider nameProvider = new KeyNameProvider();

    @Test
    public void stringName() {
        String key = nameProvider.convertStringName("key");
        assertEquals("key.s", key);
    }

    @Test
    public void intName() {
        String key = nameProvider.convertIntName("key");
        assertEquals("key.i", key);
    }

    @Test
    public void longName() {
        String key = nameProvider.convertLongName("key");
        assertEquals("key.l", key);
    }

    @Test
    public void booleanName() {
        String key = nameProvider.convertBooleanName("key");
        assertEquals("key.b", key);
    }

    @Test
    public void floatName() {
        String key = nameProvider.convertFloatName("key");
        assertEquals("key.f", key);
    }

    @Test
    public void stringSetName() {
        String key1 = nameProvider.convertStringSetName("key", 1);
        String key2 = nameProvider.convertStringSetName("key", 2);
        assertEquals("key.1.ss", key1);
        assertEquals("key.2.ss", key2);
    }
}