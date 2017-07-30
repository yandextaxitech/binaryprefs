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
public class MigrateProcessorTest {

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

        fromPreferences.edit().clear().commit();
        toPreferences.edit().clear().commit();
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

        fromPreferences.edit().clear().commit();
        toPreferences.edit().clear().commit();
    }

    @Test
    public void oneDifferentType() {
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

        fromPreferences.edit().clear().commit();
        toPreferences.edit().clear().commit();
    }
}