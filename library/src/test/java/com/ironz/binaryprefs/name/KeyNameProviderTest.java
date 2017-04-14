package com.ironz.binaryprefs.name;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KeyNameProviderTest {

    private static final String KEY = "key";
    private final KeyNameProvider nameProvider = new KeyNameProvider();

    @Test
    public void stringName() {
        String key = nameProvider.convertStringName(KEY);
        assertEquals(KEY + ".s", key);
    }

    @Test
    public void intName() {
        String key = nameProvider.convertIntName(KEY);
        assertEquals(KEY + ".i", key);
    }

    @Test
    public void longName() {
        String key = nameProvider.convertLongName(KEY);
        assertEquals(KEY + ".l", key);
    }

    @Test
    public void booleanName() {
        String key = nameProvider.convertBooleanName(KEY);
        assertEquals(KEY + ".b", key);
    }

    @Test
    public void floatName() {
        String key = nameProvider.convertFloatName(KEY);
        assertEquals(KEY + ".f", key);
    }

    @Test
    public void stringSetName() {
        String key1 = nameProvider.convertStringSetName(KEY, 1);
        String key2 = nameProvider.convertStringSetName(KEY, 2);
        assertEquals(KEY + ".1.ss", key1);
        assertEquals(KEY + ".2.ss", key2);
    }
}