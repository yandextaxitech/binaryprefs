package com.ironz.binaryprefs;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import com.ironz.binaryprefs.exception.TransactionInvalidatedException;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.impl.TestUser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public final class BinaryPreferencesTest {

    private static final String KEY_SUFFIX = "_key";

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private final String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

    private File srcDir;
    private Preferences preferences;

    @Before
    public void setUp() throws Exception {
        PreferencesCreator creator = new PreferencesCreator();
        String name = "user_preferences";
        srcDir = folder.newFolder("preferences");
        final File backupDir = folder.newFolder("backup");
        final File lockDir = folder.newFolder("lock");
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
        preferences = creator.create(name, directoryProvider);
    }

    @Test
    public void getKeysEmpty() {
        Set<String> keys = preferences.keys();

        assertTrue(keys.isEmpty());
    }

    @Test
    public void getKeysDefaultValue() {
        Set<String> keys = preferences.keys();

        assertTrue(keys.isEmpty());
    }

    @Test
    public void getKeys() {
        String stringKey = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String stringValue = "value";
        String booleanKey = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        preferences.edit()
                .putString(stringKey, stringValue)
                .putBoolean(booleanKey, true)
                .apply();

        Set<String> keys = preferences.keys();

        assertTrue(keys.contains(stringKey));
        assertTrue(keys.contains(booleanKey));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getKeysDeleteOne() {
        String stringKey = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String stringValue = "value";
        String booleanKey = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        preferences.edit()
                .putString(stringKey, stringValue)
                .putBoolean(booleanKey, true)
                .apply();

        Set<String> keys = preferences.keys();
        keys.remove(stringKey);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getKeysClear() {
        String stringKey = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String stringValue = "value";
        String booleanKey = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        preferences.edit()
                .putString(stringKey, stringValue)
                .putBoolean(booleanKey, true)
                .apply();

        Set<String> keys = preferences.keys();
        keys.clear();
    }

    @Test
    public void getKeysRemoved() {
        String stringKey = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String stringValue = "value";
        String booleanKey = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        preferences.edit()
                .putString(stringKey, stringValue)
                .putBoolean(booleanKey, true)
                .apply();

        preferences.edit()
                .clear()
                .apply();

        Set<String> keys = preferences.keys();

        assertTrue(keys.isEmpty());
    }

    @Test
    public void getAllDefaultValue() {
        Map<String, ?> all = preferences.getAll();

        assertTrue(all.isEmpty());
    }

    @Test
    public void getAll() {
        String stringKey = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String stringValue = "value";
        String booleanKey = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        preferences.edit()
                .putString(stringKey, stringValue)
                .putBoolean(booleanKey, true)
                .apply();

        Map<String, ?> all = preferences.getAll();

        Object str = all.get(stringKey);
        Object bool = all.get(booleanKey);

        assertEquals(str.getClass(), String.class);
        assertEquals(bool.getClass(), Boolean.class);
        assertEquals(str, stringValue);
        assertEquals(true, bool);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAllDeleteOne() {
        String stringKey = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String stringValue = "value";
        String booleanKey = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        preferences.edit()
                .putString(stringKey, stringValue)
                .putBoolean(booleanKey, true)
                .apply();

        Map<String, ?> all = preferences.getAll();
        all.remove(stringKey);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAllClear() {
        String stringKey = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String stringValue = "value";
        String booleanKey = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        preferences.edit()
                .putString(stringKey, stringValue)
                .putBoolean(booleanKey, true)
                .apply();

        Map<String, ?> all = preferences.getAll();
        all.clear();
    }

    @Test
    public void getAllRemoved() {
        String stringKey = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String stringValue = "value";
        String booleanKey = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        assertTrue(preferences.getAll().isEmpty());

        preferences.edit()
                .putString(stringKey, stringValue)
                .putBoolean(booleanKey, true)
                .apply();

        assertFalse(preferences.getAll().isEmpty());

        preferences.edit()
                .clear()
                .apply();

        Map<String, ?> all = preferences.getAll();

        assertTrue(all.isEmpty());
    }

    @Test
    public void stringDefaultValue() {
        String defaultValue = "default value";

        String restored = preferences.getString(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void stringValue() {
        String value = "value";
        String undefined = "undefined";

        preferences.edit()
                .putString(key, value)
                .apply();
        String restored = preferences.getString(key, undefined);

        assertEquals(value, restored);
    }

    @Test
    public void stringNullValue() {
        String undefined = "undefined";

        preferences.edit()
                .putString(key, null)
                .apply();
        String restored = preferences.getString(key, undefined);

        assertEquals(undefined, restored);
    }

    @Test
    public void intValue() {
        int value = Integer.MAX_VALUE;

        preferences.edit()
                .putInt(key, value)
                .apply();
        int restored = preferences.getInt(key, 0);

        assertEquals(value, restored);
    }

    @Test
    public void intDefaultValue() {
        int defaultValue = Integer.MAX_VALUE;

        int restored = preferences.getInt(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void longValue() {
        long value = Long.MAX_VALUE;

        preferences.edit()
                .putLong(key, value)
                .apply();
        long restored = preferences.getLong(key, 0L);

        assertEquals(value, restored);
    }

    @Test
    public void longDefaultValue() {
        long defaultValue = Long.MAX_VALUE;

        long restored = preferences.getLong(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void floatValue() {
        float value = Float.MAX_VALUE;

        preferences.edit()
                .putFloat(key, value)
                .apply();
        float restored = preferences.getFloat(key, .0f);

        assertEquals(value, restored, .0f);
    }

    @Test
    public void floatDefaultValue() {
        float defaultValue = Float.MAX_VALUE;

        float restored = preferences.getFloat(key, defaultValue);

        assertEquals(defaultValue, restored, .0f);
    }

    @Test
    public void booleanValue() {
        preferences.edit()
                .putBoolean(key, true)
                .apply();
        boolean restored = preferences.getBoolean(key, false);

        assertEquals(true, restored);
    }

    @Test
    public void booleanDefaultValue() {
        boolean restored = preferences.getBoolean(key, true);

        assertEquals(true, restored);
    }

    @Test
    public void stringSetValue() {
        Set<String> value = new HashSet<>();
        value.add("one");
        value.add("two");
        value.add("tree");

        preferences.edit()
                .putStringSet(key, value)
                .apply();
        Set<String> restored = preferences.getStringSet(key, new HashSet<String>());

        assertEquals(value, restored);
    }

    @Test
    public void stringSetNullValue() {
        Set<String> defaultValue = new HashSet<>();

        preferences.edit()
                .putStringSet(key, null)
                .apply();
        Set<String> restored = preferences.getStringSet(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void stringSetDefaultValue() {
        Set<String> defaultValue = new HashSet<>();
        defaultValue.add("one");
        defaultValue.add("two");
        defaultValue.add("tree");

        Set<String> restored = preferences.getStringSet(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void persistableValue() {
        TestUser value = TestUser.create();

        preferences.edit()
                .putPersistable(key, value)
                .apply();
        TestUser restored = preferences.getPersistable(key, new TestUser());

        assertEquals(value, restored);
    }

    @Test
    public void persistableCompareValues() {
        TestUser value = TestUser.create();

        preferences.edit()
                .putPersistable(key, value)
                .apply();
        TestUser restored = preferences.getPersistable(key, new TestUser());
        TestUser restored2 = preferences.getPersistable(key, new TestUser());
        restored.setName(restored.getName() + "modified");

        assertNotEquals(value, restored);
        assertNotEquals(restored, restored2);
    }

    @Test
    public void persistableNullValue() {
        TestUser defaultValue = new TestUser();

        preferences.edit()
                .putPersistable(key, null)
                .apply();
        TestUser restored = preferences.getPersistable(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void persistableDefaultValue() {
        TestUser defaultValue = TestUser.create();

        TestUser restored = preferences.getPersistable(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void byteValue() {
        byte value = Byte.MAX_VALUE;
        byte defaultValue = 0;

        preferences.edit()
                .putByte(key, value)
                .apply();
        byte restored = preferences.getByte(key, defaultValue);

        assertEquals(value, restored);
    }

    @Test
    public void byteDefaultValue() {
        byte defaultValue = Byte.MAX_VALUE;

        byte restored = preferences.getByte(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void shortValue() {
        short value = Short.MAX_VALUE;
        short defaultValue = 0;

        preferences.edit()
                .putShort(key, value)
                .apply();
        short restored = preferences.getShort(key, defaultValue);

        assertEquals(value, restored);
    }

    @Test
    public void shortDefaultValue() {
        short defaultValue = Short.MAX_VALUE;

        short restored = preferences.getShort(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void charValue() {
        char value = Character.MAX_VALUE;
        char defaultValue = 0;

        preferences.edit()
                .putChar(key, value)
                .apply();
        char restored = preferences.getChar(key, defaultValue);

        assertEquals(value, restored);
    }

    @Test
    public void charDefaultValue() {
        char defaultValue = Character.MAX_VALUE;

        char restored = preferences.getChar(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void doubleValue() {
        double value = Double.MAX_VALUE;
        double defaultValue = 0;

        preferences.edit()
                .putDouble(key, value)
                .apply();
        double restored = preferences.getDouble(key, defaultValue);

        assertEquals(value, restored, .0);
    }

    @Test
    public void doubleDefaultValue() {
        double defaultValue = Double.MAX_VALUE;

        double restored = preferences.getDouble(key, defaultValue);

        assertEquals(defaultValue, restored, .0);
    }

    @Test
    public void clear() {
        String value = "value";
        String undefined = "undefined value";

        preferences.edit()
                .putString(key, value)
                .apply();
        String restored = preferences.getString(key, undefined);

        assertEquals(value, restored);

        preferences.edit()
                .clear()
                .apply();
        String restored2 = preferences.getString(key, undefined);

        assertFalse(preferences.contains(restored2));
    }

    @Test
    public void clearFirst() {
        String value = "value";
        String undefined = "undefined value";

        preferences.edit()
                .putString(key, value)
                .clear()
                .apply();
        String restored = preferences.getString(key, undefined);

        assertEquals(value, restored);
    }

    @Test
    public void remove() {
        String value = "value";
        String undefined = "undefined";

        preferences.edit()
                .putString(key, value)
                .apply();

        preferences.edit().remove(key).apply();
        String restored = preferences.getString(key, undefined);

        assertEquals(undefined, restored);
    }

    @Test
    public void removeFirst() {
        String value = "value";
        String undefined = "undefined";

        preferences.edit()
                .putString(key, value)
                .remove(key)
                .apply();
        String restored = preferences.getString(key, undefined);

        assertEquals(value, restored);
    }

    @Test
    public void commitTrue() {
        String value = "value";
        String undefined = "undefined";

        boolean commit = preferences.edit()
                .putString(key, value)
                .commit();
        String restored = preferences.getString(key, undefined);

        assertTrue(commit);
        assertEquals(value, restored);
    }

    @Test
    public void commitFalse() {
        String value = "value";
        String undefined = "undefined";

        assertTrue(srcDir.delete());

        boolean commit = preferences.edit()
                .putString(key, value)
                .commit();
        String restored = preferences.getString(key, undefined);

        assertFalse(commit);
        assertEquals(value, restored);
    }

    @Test(expected = TransactionInvalidatedException.class)
    public void applyTwice() {
        String value = "value";

        PreferencesEditor editor = preferences.edit()
                .putString(key, value);
        editor.apply();
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    @Test(expected = TransactionInvalidatedException.class)
    public void commitTwice() {
        String value = "value";

        PreferencesEditor editor = preferences.edit()
                .putString(key, value);
        editor.commit();
        editor.commit();
    }

    @Test
    public void registeredListenerChanges() {
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
        preferences.registerOnSharedPreferenceChangeListener(listener);
        preferences.edit()
                .putString(key, value)
                .apply();

        assertTrue(changed.get());
        assertEquals(value, preferences.getString(key, undefined));

        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Test
    public void unregisteredListenerChanges() {
        String value = "value";

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                throw new UnsupportedOperationException("This method should never be invoked!");
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);

        preferences.edit()
                .putString(key, value)
                .apply();
    }
}