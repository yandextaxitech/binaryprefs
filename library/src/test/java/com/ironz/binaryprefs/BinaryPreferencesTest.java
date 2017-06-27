package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.cache.ConcurrentCacheProviderImpl;
import com.ironz.binaryprefs.encryption.AesByteEncryptionImpl;
import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.events.SimpleEventBridgeImpl;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.file.adapter.NioFileAdapter;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.impl.TestFileTransactionImpl;
import com.ironz.binaryprefs.impl.TestUser;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.lock.SimpleLockFactoryImpl;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
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

        File newFolder = folder.newFolder();
        String baseDirectory = newFolder.getAbsolutePath();
        FileAdapter fileAdapter = new NioFileAdapter();
        ExceptionHandler exceptionHandler = ExceptionHandler.IGNORE;
        FileTransaction fileTransaction = new TestFileTransactionImpl(baseDirectory, fileAdapter, exceptionHandler);
        ByteEncryption byteEncryption = new AesByteEncryptionImpl("1111111111111111".getBytes(), "0000000000000000".getBytes());
        CacheProvider cacheProvider = new ConcurrentCacheProviderImpl();
        PersistableRegistry persistableRegistry = new PersistableRegistry();
        persistableRegistry.register(TestUser.KEY, TestUser.class);
        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
        LockFactory lockFactory = new SimpleLockFactoryImpl();
        EventBridge eventsBridge = new SimpleEventBridgeImpl();
        PreferencesInitializeListener initializeListener = new PreferencesInitializeListener() {
            @Override
            public void onCompleted() {

            }
        };

        preferences = new BinaryPreferences(
                fileTransaction,
                byteEncryption,
                exceptionHandler,
                eventsBridge,
                cacheProvider,
                TaskExecutor.DEFAULT,
                serializerFactory,
                lockFactory,
                initializeListener
        );
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
    public void stringNullValue() {
        String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String undefined = "undefined";

        preferences.edit()
                .putString(key, null)
                .apply();
        String restored = preferences.getString(key, undefined);

        assertEquals(undefined, restored);
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
        String key = Set.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        Set<String> defaultValue = new HashSet<>();

        preferences.edit()
                .putStringSet(key, null)
                .apply();
        Set<String> restored = preferences.getStringSet(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void stringSetDefaultValue() {
        String key = Set.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        Set<String> defaultValue = new HashSet<>();
        defaultValue.add("one");
        defaultValue.add("two");
        defaultValue.add("tree");

        Set<String> restored = preferences.getStringSet(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void persistableValue() {
        String key = TestUser.KEY;
        TestUser value = TestUser.create();

        preferences.edit()
                .putPersistable(key, value)
                .apply();
        TestUser restored = preferences.getPersistable(key, new TestUser());

        assertEquals(value, restored);
    }

    @Test
    public void persistableCompareValues() {
        String key = TestUser.KEY;
        TestUser value = TestUser.create();

        preferences.edit()
                .putPersistable(key, value)
                .apply();
        TestUser restored = preferences.getPersistable(key, new TestUser());

        restored.setName(restored.getName() + "modified");

        assertNotEquals(value, restored);
    }

    @Test
    public void persistableNullValue() {
        String key = TestUser.KEY;
        TestUser defaultValue = new TestUser();

        preferences.edit()
                .putPersistable(key, null)
                .apply();
        TestUser restored = preferences.getPersistable(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void persistableDefaultValue() {
        String key = TestUser.KEY;
        TestUser defaultValue = TestUser.create();

        TestUser restored = preferences.getPersistable(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void byteValue() {
        String key = byte.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
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
        String key = byte.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        byte defaultValue = Byte.MAX_VALUE;

        byte restored = preferences.getByte(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void shortValue() {
        String key = short.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
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
        String key = short.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        short defaultValue = Short.MAX_VALUE;

        short restored = preferences.getShort(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void charValue() {
        String key = char.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
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
        String key = char.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        char defaultValue = Character.MAX_VALUE;

        char restored = preferences.getChar(key, defaultValue);

        assertEquals(defaultValue, restored);
    }

    @Test
    public void doubleValue() {
        String key = double.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
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
        String key = double.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        double defaultValue = Double.MAX_VALUE;

        double restored = preferences.getDouble(key, defaultValue);

        assertEquals(defaultValue, restored, .0);
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
    public void commitTrue() {
        String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
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
        String key = String.class.getSimpleName().toLowerCase() + KEY_SUFFIX;
        String value = "value";
        String undefined = "undefined";

        folder.delete();

        boolean commit = preferences.edit()
                .putString(key, value)
                .commit();
        String restored = preferences.getString(key, undefined);

        assertFalse(commit);
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
                throw new UnsupportedOperationException("This method should never be invoked!");
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
        preferences.edit().putString(key, value).apply();
    }
}