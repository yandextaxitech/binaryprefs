package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.cache.ConcurrentCacheProviderImpl;
import com.ironz.binaryprefs.encryption.AesValueEncryptionImpl;
import com.ironz.binaryprefs.encryption.KeyEncryption;
import com.ironz.binaryprefs.encryption.ValueEncryption;
import com.ironz.binaryprefs.encryption.XorKeyEncryptionImpl;
import com.ironz.binaryprefs.event.EventBridge;
import com.ironz.binaryprefs.event.SimpleEventBridgeImpl;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.file.adapter.NioFileAdapter;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.MultiProcessTransactionImpl;
import com.ironz.binaryprefs.impl.TestUser;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.lock.SimpleLockFactoryImpl;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import com.ironz.binaryprefs.task.TaskExecutor;
import com.ironz.binaryprefs.task.TestTaskExecutorImpl;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public final class PreferencesCreator {

    public Preferences create(String name, TemporaryFolder folder) {
        try {
            final File srcDir = folder.newFolder();
            final File backupDir = folder.newFolder();
            final File lockDir = folder.newFolder();
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
            return create(name, directoryProvider);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Preferences create(String name, DirectoryProvider directoryProvider) {

        Map<String, ReadWriteLock> locks = new ConcurrentHashMap<>();
        Map<String, Lock> processLocks = new ConcurrentHashMap<>();
        Map<String, Map<String, Object>> allCaches = new ConcurrentHashMap<>();

        return create(name, directoryProvider, locks, processLocks, allCaches);
    }

    Preferences create(String name,
                       DirectoryProvider directoryProvider,
                       Map<String, ReadWriteLock> locks,
                       Map<String, Lock> processLocks,
                       Map<String, Map<String, Object>> allCaches) {

        FileAdapter fileAdapter = new NioFileAdapter(directoryProvider);
        ExceptionHandler exceptionHandler = ExceptionHandler.IGNORE;

        LockFactory lockFactory = new SimpleLockFactoryImpl(name, directoryProvider, locks, processLocks);
        ValueEncryption valueEncryption = new AesValueEncryptionImpl("1111111111111111".getBytes(), "0000000000000000".getBytes());
        KeyEncryption keyEncryption = new XorKeyEncryptionImpl("1111111111111110".getBytes());
        FileTransaction fileTransaction = new MultiProcessTransactionImpl(fileAdapter, lockFactory, valueEncryption, keyEncryption);
        CacheProvider cacheProvider = new ConcurrentCacheProviderImpl(name, allCaches);
        TaskExecutor executor = new TestTaskExecutorImpl(name, exceptionHandler);
        PersistableRegistry persistableRegistry = new PersistableRegistry();
        persistableRegistry.register(TestUser.KEY, TestUser.class);
        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
        EventBridge eventsBridge = new SimpleEventBridgeImpl(name);
        return new BinaryPreferences(
                fileTransaction,
                eventsBridge,
                cacheProvider,
                executor,
                serializerFactory,
                lockFactory
        );
    }
}