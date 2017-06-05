package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.cache.ConcurrentCacheProviderImpl;
import com.ironz.binaryprefs.encryption.AesByteEncryptionImpl;
import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.events.SimpleEventBridgeImpl;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.file.NioFileAdapter;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.impl.TestAddress;
import com.ironz.binaryprefs.impl.TestUser;
import com.ironz.binaryprefs.task.TaskExecutor;
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

    @Before
    public void setUp() throws Exception {
        final File folder = this.folder.newFolder();
        ByteEncryption byteEncryption = new AesByteEncryptionImpl("1111111111111111".getBytes(), "0000000000000000".getBytes());
        DirectoryProvider directoryProvider = new DirectoryProvider() {
            @Override
            public File getBaseDirectory() {
                return folder;
            }
        };
        FileAdapter fileAdapter = new NioFileAdapter(directoryProvider, byteEncryption);
        EventBridge eventsBridge = new SimpleEventBridgeImpl();
        CacheProvider cacheProvider = new ConcurrentCacheProviderImpl();
        preferences = new BinaryPreferences(fileAdapter, ExceptionHandler.IGNORE, eventsBridge, cacheProvider, TaskExecutor.DEFAULT);
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
    public void intDefaultValue() {
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
    public void longDefaultValue() {
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
    public void floatDefaultValue() {
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
    public void booleanDefaultValue() {
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
    public void stringSetDefaultValue() {
        String key = Set.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        HashSet<String> defaultValue = new HashSet<>();
        defaultValue.add("one");
        defaultValue.add("two");
        defaultValue.add("tree");

        Set<String> restored = preferences.getStringSet(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void persistableValue() {
        String key = TestUser.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        TestUser value = new TestUser();
        value.setName("John");
        value.setAge((short) 21);
        value.setSex('M');
        value.setMarried(true);
        value.setPostal(1234567890L);
        value.setChild((byte) 19);
        value.addAddresses(new TestAddress("USA", "New York", "1th", 25));
        value.addAddresses(new TestAddress("Russia", "Moscow", "Red Square", 1));

        preferences.edit()
                .putPersistable(key, value)
                .apply();
        TestUser restored = preferences.getPersistable(TestUser.class, key, new TestUser());

        assertEquals(value, restored);
    }

    @Test
    public void persistableDefaultValue() {
        String key = TestUser.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        TestUser defaultValue = new TestUser();
        defaultValue.setName("John");
        defaultValue.setAge((short) 21);
        defaultValue.setSex('M');
        defaultValue.setMarried(true);
        defaultValue.setPostal(1234567890L);
        defaultValue.setChild((byte) 19);
        defaultValue.addAddresses(new TestAddress("USA", "New York", "1th", 25));
        defaultValue.addAddresses(new TestAddress("Russia", "Moscow", "Red Square", 1));

        TestUser restored = preferences.getPersistable(TestUser.class, key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void clear() {
        String key = "key";
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
        String key = "key";
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
        String undefined = "undefined";

        preferences.edit()
                .putString(key, value)
                .remove(key)
                .apply();
        String restored = preferences.getString(key, undefined);

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

        assertTrue(changed.get());
        assertEquals(value, preferences.getString(key, undefined));
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
}