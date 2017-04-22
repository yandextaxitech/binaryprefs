package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.exception.ExceptionHandlerImpl;
import com.ironz.binaryprefs.files.FileAdapter;
import com.ironz.binaryprefs.files.NioFileAdapter;
import com.ironz.binaryprefs.name.KeyNameProvider;
import com.ironz.binaryprefs.name.SimpleKeyNameProviderImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class BinaryPreferencesTest {

    private static final String KEY_SUFFIX = "_key";

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();
    private BinaryPreferences preferences;

    @Before
    public void setUp() throws Exception {
        ExceptionHandler exceptionHandler = new ExceptionHandlerImpl();
        FileAdapter adapter = new NioFileAdapter(folder.newFolder());
        KeyNameProvider keyNameProvider = new SimpleKeyNameProviderImpl();
        preferences = new BinaryPreferences(adapter, exceptionHandler, keyNameProvider);
    }

    @Test
    public void stringValue() {

        String key = String.class.getSimpleName() + KEY_SUFFIX;
        String value = "value";

        preferences.edit()
                .putString(key, value)
                .apply();
        String restored = preferences.getString(key, "default");

        assertEquals(value, restored);
    }

    @Test
    public void intValue() {

        String key = int.class.getSimpleName() + KEY_SUFFIX;
        int value = Integer.MAX_VALUE;

        preferences.edit()
                .putInt(key, value)
                .apply();
        int restored = preferences.getInt(key, 0);

        assertEquals(value, restored);
    }

    @Test
    public void longValue() {

        String key = long.class.getSimpleName() + KEY_SUFFIX;
        long value = Long.MAX_VALUE;

        preferences.edit()
                .putLong(key, value)
                .apply();
        long restored = preferences.getLong(key, 0L);

        assertEquals(value, restored);
    }

    @Test
    public void floatValue() {

        String key = float.class.getSimpleName() + KEY_SUFFIX;
        float value = Float.MAX_VALUE;

        preferences.edit()
                .putFloat(key, value)
                .apply();
        float restored = preferences.getFloat(key, .0f);

        assertEquals(value, restored, .0f);
    }

    @Test
    public void booleanValue() {

        String key = boolean.class.getSimpleName() + KEY_SUFFIX;

        preferences.edit()
                .putBoolean(key, true)
                .apply();
        boolean restored = preferences.getBoolean(key, false);

        assertEquals(true, restored);
    }

    @Test
    public void stringSetValue() {

        String key = Set.class.getSimpleName() + KEY_SUFFIX;
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

        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                atomicBoolean.set(true);
                assertEquals(key, s);
                assertEquals(value, sharedPreferences.getString(key, undefined));
            }
        });
        preferences.edit().putString(key, value).apply();
        assertEquals(value, preferences.getString(key, undefined));
        assertTrue(atomicBoolean.get());
    }

    @Test
    public void removeListeners() {

        String key = "key";
        String value = "value";

        final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                throw new UnsupportedOperationException("This method should not never be called!");
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
        preferences.edit().putString(key, value).apply();
    }

    @Test
    public void commit() {
        String key = String.class.getSimpleName() + KEY_SUFFIX;
        String value = "value";
        assertTrue(preferences.edit()
                .putString(key, value)
                .commit());
    }
}