package com.ironz.binaryprefs;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MultipleBinaryPreferencesTest {

    private static final String KEY_SUFFIX = "_key";

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private Preferences firstPreferencesInstance;
    private Preferences secondPreferencesInstance;
    private File srcDir;
    private File backupDir;
    private File lockDir;

    @Before
    public void setUp() throws Exception {
        srcDir = folder.newFolder("preferences");
        backupDir = folder.newFolder("backup");
        lockDir = folder.newFolder("lock");
        firstPreferencesInstance = createPreferences();
        secondPreferencesInstance = createPreferences();
    }

    private BinaryPreferences createPreferences() throws IOException {
        String name = "user_preferences";
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
        TaskExecutor executor = new TestTaskExecutorImpl();
        PersistableRegistry persistableRegistry = new PersistableRegistry();
        persistableRegistry.register(TestUser.KEY, TestUser.class);
        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
        EventBridge eventsBridge = new SimpleEventBridgeImpl();

        return new BinaryPreferences(
                name,
                fileTransaction,
                byteEncryption,
                exceptionHandler,
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
}