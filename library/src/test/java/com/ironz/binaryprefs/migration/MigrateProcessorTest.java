package com.ironz.binaryprefs.migration;

import android.annotation.SuppressLint;
import com.ironz.binaryprefs.Preferences;
import com.ironz.binaryprefs.PreferencesCreator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Map;

import static org.junit.Assert.*;

@SuppressLint("ApplySharedPref")
public final class MigrateProcessorTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private MigrateProcessor migrateProcessor;
    private PreferencesCreator creator;

    @Before
    public void setUp() {
        creator = new PreferencesCreator();
        migrateProcessor = new MigrateProcessor();
    }

    @Test
    public void emptyOne() {
        Preferences fromPreferences = creator.create("from", folder);
        Preferences toPreferences = creator.create("to", folder);
        String key = "key";
        String value = "value";

        toPreferences.edit().putString(key, value).commit();

        migrateProcessor.add(fromPreferences);
        migrateProcessor.migrateTo(toPreferences);

        Map<String, ?> toAll = toPreferences.getAll();

        assertFalse(toAll.isEmpty());
        assertEquals(1, toAll.size());
        assertEquals(value, toAll.get(key));

        Map<String, ?> fromAll = fromPreferences.getAll();

        assertTrue(fromAll.isEmpty());
    }

    @Test
    public void one() {
        Preferences fromPreferences = creator.create("from", folder);
        Preferences toPreferences = creator.create("to", folder);
        String key = "key";
        String value = "value";
        String key1 = "key1";
        String value1 = "value1";

        fromPreferences.edit().putString(key1, value1).commit();
        toPreferences.edit().putString(key, value).commit();

        migrateProcessor.add(fromPreferences);
        migrateProcessor.migrateTo(toPreferences);

        Map<String, ?> toAll = toPreferences.getAll();

        assertFalse(toAll.isEmpty());
        assertEquals(2, toAll.size());
        assertEquals(value, toAll.get(key));
        assertEquals(value1, toAll.get(key1));

        Map<String, ?> fromAll = fromPreferences.getAll();

        assertTrue(fromAll.isEmpty());
    }

    @Test
    public void oneDifferentTypesCollision() {
        Preferences fromPreferences = creator.create("from", folder);
        Preferences toPreferences = creator.create("to", folder);
        String key = "key";
        String value = "value";
        String key1 = "key";
        int value1 = 1;

        fromPreferences.edit().putInt(key1, value1).commit();
        toPreferences.edit().putString(key, value).commit();

        migrateProcessor.add(fromPreferences);
        migrateProcessor.migrateTo(toPreferences);

        Map<String, ?> toAll = toPreferences.getAll();

        assertFalse(toAll.isEmpty());
        assertEquals(1, toAll.size());
        assertEquals(value1, toAll.get(key1));

        Map<String, ?> fromAll = fromPreferences.getAll();

        assertTrue(fromAll.isEmpty());
    }

    @Test
    public void multipleCollision() {
        Preferences fromPreferences = creator.create("from", folder);
        Preferences fromPreferences2 = creator.create("from2", folder);
        Preferences toPreferences = creator.create("to", folder);
        String key = "key";
        String value = "value";

        fromPreferences.edit().putString(key, value).commit();
        fromPreferences2.edit().putString(key, value).commit();

        migrateProcessor.add(fromPreferences);
        migrateProcessor.add(fromPreferences2);
        migrateProcessor.migrateTo(toPreferences);

        Map<String, ?> toAll = toPreferences.getAll();

        assertFalse(toAll.isEmpty());
        assertEquals(1, toAll.size());
        assertEquals(value, toAll.get(key));

        Map<String, ?> fromAll = fromPreferences.getAll();
        Map<String, ?> fromAll2 = fromPreferences2.getAll();

        assertTrue(fromAll.isEmpty());
        assertTrue(fromAll2.isEmpty());
    }

    @Test
    public void multipleDifferentTypeCollision() {
        Preferences fromPreferences = creator.create("from", folder);
        Preferences fromPreferences2 = creator.create("from2", folder);
        Preferences toPreferences = creator.create("to", folder);
        String key = "key";
        String value = "value";
        String key1 = "key";
        int value1 = 1;

        fromPreferences.edit().putString(key, value).commit();
        fromPreferences2.edit().putInt(key1, value1).commit();

        migrateProcessor.add(fromPreferences);
        migrateProcessor.add(fromPreferences2);
        migrateProcessor.migrateTo(toPreferences);

        Map<String, ?> toAll = toPreferences.getAll();

        assertFalse(toAll.isEmpty());
        assertEquals(1, toAll.size());
        assertEquals(value1, toAll.get(key));

        Map<String, ?> fromAll = fromPreferences.getAll();
        Map<String, ?> fromAll2 = fromPreferences2.getAll();

        assertTrue(fromAll.isEmpty());
        assertTrue(fromAll2.isEmpty());
    }
}