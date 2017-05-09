package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.encryption.AesByteEncryptionImpl;
import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.DirectoryProvider;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.file.NioFileAdapter;
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

    private Preferences preferences;
    private FileAdapter fileAdapter;

    @Before
    public void setUp() throws Exception {
        byte[] key = "0000000000000000".getBytes();
        byte[] iv = "1111111111111111".getBytes();
        final File folder = this.folder.newFolder();
        ByteEncryption encryption = new AesByteEncryptionImpl(key, iv);
        fileAdapter = new NioFileAdapter(new DirectoryProvider() {
            @Override
            public File getBaseDirectory() {
                return folder;
            }
        });
        preferences = new BinaryPreferences(fileAdapter, ExceptionHandler.IGNORE);
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

        assertEquals(all.get(stringKey).getClass(), String.class);
        assertEquals(all.get(booleanKey).getClass(), Boolean.class);
    }

    @Test
    public void stringDefaultValue() {
        String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String defaultValue = "default value";

        String restored = preferences.getString(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void stringValue() {

        String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String value = "value";
        String undefined = "undefined";

        preferences.edit()
                .putString(key, value)
                .apply();
        String restored = preferences.getString(key, undefined);

        assertEquals(value, restored);
    }

    @Test
    public void intValue() {

        String key = int.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        int value = Integer.MAX_VALUE;

        preferences.edit()
                .putInt(key, value)
                .apply();
        int restored = preferences.getInt(key, 0);

        assertEquals(value, restored);
    }

    @Test
    public void defaultIntValue() {
        String key = int.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        int defaultValue = Integer.MAX_VALUE;

        int restored = preferences.getInt(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void longValue() {

        String key = long.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        long value = Long.MAX_VALUE;

        preferences.edit()
                .putLong(key, value)
                .apply();
        long restored = preferences.getLong(key, 0L);

        assertEquals(value, restored);
    }

    @Test
    public void defaultLongValue() {

        String key = long.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        long defaultValue = Long.MAX_VALUE;

        long restored = preferences.getLong(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void floatValue() {

        String key = float.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        float value = Float.MAX_VALUE;

        preferences.edit()
                .putFloat(key, value)
                .apply();
        float restored = preferences.getFloat(key, .0f);

        assertEquals(value, restored, .0f);
    }

    @Test
    public void defaultFloatValue() {

        String key = float.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        float defaultValue = Float.MAX_VALUE;

        float restored = preferences.getFloat(key, defaultValue);

        assertEquals(defaultValue, restored, .0f);
    }

    @Test
    public void booleanValue() {

        String key = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        preferences.edit()
                .putBoolean(key, true)
                .apply();
        boolean restored = preferences.getBoolean(key, false);

        assertEquals(true, restored);
    }

    @Test
    public void defaultBooleanValue() {

        String key = boolean.class.getSimpleName().toLowerCase() + KEY_SUFFIX;

        boolean restored = preferences.getBoolean(key, true);

        assertEquals(true, restored);
    }

    @Test
    public void stringSetValue() {

        String key = Set.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        HashSet<String> value = new HashSet<>();
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
    public void defaultStringSetValue() {

        String key = Set.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        HashSet<String> defaultValue = new HashSet<>();
        defaultValue.add("one");
        defaultValue.add("two");
        defaultValue.add("tree");

        Set<String> restored = preferences.getStringSet(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void clearFirst() {

        String key = "key";
        String value = "value";

        preferences.edit()
                .putString(key, value)
                .clear()
                .apply();
        String restored = preferences.getString(key, value);

        assertEquals(value, restored);
    }

    @Test
    public void remove() {

        String key = "key";
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

        String key = "key";
        String value = "value";

        preferences.edit()
                .putString(key, value)
                .remove(key)
                .apply();
        String restored = preferences.getString(key, value);

        assertEquals(value, restored);
    }

    @Test
    public void listeners() {

        final String key = "key";
        final String value = "value";
        final String undefined = "undefined";

        final AtomicBoolean changed = new AtomicBoolean(false);

        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                changed.set(true);
                assertEquals(key, s);
                assertEquals(value, sharedPreferences.getString(key, undefined));
            }
        });
        preferences.edit().putString(key, value).apply();
        assertEquals(value, preferences.getString(key, undefined));
        assertTrue(changed.get());
    }

    @Test
    public void removeListeners() {

        String key = "key";
        String value = "value";

        final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                throw new UnsupportedOperationException("This method should never be called!");
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
        preferences.edit().putString(key, value).apply();
    }

    // TODO: 4/26/17 reimplement and restore test annotation
//    @Test
    public void commit() {

        String key = String.class.getSimpleName() + KEY_SUFFIX;
        String value = "value";

        assertTrue(preferences.edit()
                .putString(key, value)
                .commit());

        folder.delete();

        assertFalse(preferences.edit()
                .putString(key, value)
                .commit());
    }
}