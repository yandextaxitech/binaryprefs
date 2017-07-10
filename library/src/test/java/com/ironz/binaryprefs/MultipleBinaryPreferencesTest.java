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
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.MultiProcessTransactionImpl;
import com.ironz.binaryprefs.impl.TestTaskExecutorImpl;
import com.ironz.binaryprefs.impl.TestUser;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.lock.SimpleLockFactoryImpl;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import com.ironz.binaryprefs.task.TaskExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class MultipleBinaryPreferencesTest {

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
        firstPreferencesInstance = createPreferences("user_preferences");
        secondPreferencesInstance = createPreferences("user_preferences");
        thirdPreferencesInstance = createPreferences("user_preferences_2");
    }

    private BinaryPreferences createPreferences(String name) throws IOException {
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
        FileAdapter fileAdapter = new NioFileAdapter(directoryProvider);
        ExceptionHandler exceptionHandler = ExceptionHandler.IGNORE;
        LockFactory lockFactory = new SimpleLockFactoryImpl(name, directoryProvider);
        FileTransaction fileTransaction = new MultiProcessTransactionImpl(fileAdapter, lockFactory);
        ByteEncryption byteEncryption = new AesByteEncryptionImpl("1111111111111111".getBytes(), "0000000000000000".getBytes());
        CacheProvider cacheProvider = new ConcurrentCacheProviderImpl(name);
        TaskExecutor executor = new TestTaskExecutorImpl(name, exceptionHandler);
        PersistableRegistry persistableRegistry = new PersistableRegistry();
        persistableRegistry.register(TestUser.KEY, TestUser.class);
        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
        EventBridge eventsBridge = new SimpleEventBridgeImpl(name);

        return new BinaryPreferences(
                fileTransaction,
                byteEncryption,
                eventsBridge,
                cacheProvider,
                executor,
                serializerFactory,
                lockFactory
        );
    }

    @After
    public void tearDown() {
        firstPreferencesInstance.edit()
                .clear()
                .apply();
        secondPreferencesInstance.edit()
                .clear()
                .apply();
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