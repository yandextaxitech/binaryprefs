package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static org.junit.Assert.*;

public final class MultipleBinaryPreferencesTest {

    private static final String KEY_SUFFIX = "_key";

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private File srcDir;
    private File backupDir;
    private File lockDir;

    private Preferences firstPreferencesInstance;
    private Preferences secondPreferencesInstance;
    private Preferences thirdPreferencesInstance;

    @Before
    public void setUp() throws Exception {
        srcDir = folder.newFolder("preferences");
        backupDir = folder.newFolder("backup");
        lockDir = folder.newFolder("lock");
        Map<String, ReadWriteLock> locks = new ConcurrentHashMap<>();
        Map<String, Lock> globalLocks = new ConcurrentHashMap<>();
        Map<String, Map<String, Object>> allCaches = new ConcurrentHashMap<>();
        firstPreferencesInstance = createPreferences("user_preferences", locks, globalLocks, allCaches);
        secondPreferencesInstance = createPreferences("user_preferences", locks, globalLocks, allCaches);
        thirdPreferencesInstance = createPreferences("user_preferences_2", locks, globalLocks, allCaches);
    }

    private Preferences createPreferences(String name,
                                          Map<String, ReadWriteLock> locks,
                                          Map<String, Lock> globalLocks,
                                          Map<String, Map<String, Object>> allCaches) throws IOException {

        DirectoryProvider directoryProvider = new DirectoryProvider() {
            @Override
            public File getStoreDirectory() {
                return srcDir;
            }
            @Override
            public File getBackupDirectory() {
                return backupDir;
            }
            @Override
            public File getLockDirectory() {
                return lockDir;
            }
        };
        PreferencesCreator creator = new PreferencesCreator();
        return creator.create(name, directoryProvider, locks, globalLocks, allCaches);
    }

    @Test
    public void addValue() {
        String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String value = "value";
        String undefined = "undefined";

        firstPreferencesInstance.edit()
                .putString(key, value)
                .apply();
        String restored = firstPreferencesInstance.getString(key, undefined);
        String restored2 = secondPreferencesInstance.getString(key, undefined);

        assertEquals(value, restored);
        assertEquals(value, restored2);
    }

    @Test
    public void removeValue() {
        String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String value = "value";

        firstPreferencesInstance.edit()
                .putString(key, value)
                .apply();
        secondPreferencesInstance.edit()
                .remove(key)
                .apply();

        assertFalse(firstPreferencesInstance.contains(key));
        assertFalse(secondPreferencesInstance.contains(key));
    }

    @Test
    public void clearAllValues() {
        String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String value = "value";

        firstPreferencesInstance.edit()
                .putString(key, value)
                .apply();
        secondPreferencesInstance.edit()
                .clear()
                .apply();

        assertFalse(firstPreferencesInstance.contains(key));
        assertFalse(secondPreferencesInstance.contains(key));
    }

    @Test
    public void registeredListenerChanges() {
        final String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        final String value = "value";
        final String undefined = "undefined";

        final AtomicBoolean changed = new AtomicBoolean(false);

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                changed.set(true);
                assertEquals(key, s);
                assertEquals(value, sharedPreferences.getString(key, undefined));
            }
        };
        secondPreferencesInstance.registerOnSharedPreferenceChangeListener(listener);
        firstPreferencesInstance.edit()
                .putString(key, value)
                .apply();

        assertTrue(changed.get());
        assertEquals(value, secondPreferencesInstance.getString(key, undefined));
    }

    @Test
    public void unregisteredListenerChanges() {
        String key = "key";
        String value = "value";

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                throw new UnsupportedOperationException("This method should never be invoked!");
            }
        };
        secondPreferencesInstance.registerOnSharedPreferenceChangeListener(listener);
        secondPreferencesInstance.unregisterOnSharedPreferenceChangeListener(listener);

        firstPreferencesInstance.edit()
                .putString(key, value)
                .apply();
    }

    @Test
    public void anotherPreferences() {
        String key = "key";
        String value = "value";

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                throw new UnsupportedOperationException("This method should never be invoked!");
            }
        };
        thirdPreferencesInstance.registerOnSharedPreferenceChangeListener(listener);

        firstPreferencesInstance.edit()
                .putString(key, value)
                .apply();

        thirdPreferencesInstance.unregisterOnSharedPreferenceChangeListener(listener);
    }
}