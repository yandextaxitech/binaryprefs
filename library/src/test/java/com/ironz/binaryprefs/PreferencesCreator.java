package com.ironz.binaryprefs;

import com.ironz.binaryprefs.cache.candidates.CacheCandidateProvider;
import com.ironz.binaryprefs.cache.candidates.ConcurrentCacheCandidateProvider;
import com.ironz.binaryprefs.cache.provider.CacheProvider;
import com.ironz.binaryprefs.cache.provider.ConcurrentCacheProvider;
import com.ironz.binaryprefs.encryption.AesValueEncryption;
import com.ironz.binaryprefs.encryption.KeyEncryption;
import com.ironz.binaryprefs.encryption.ValueEncryption;
import com.ironz.binaryprefs.encryption.XorKeyEncryption;
import com.ironz.binaryprefs.event.EventBridge;
import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.event.SimpleEventBridge;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.file.adapter.NioFileAdapter;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.MultiProcessTransaction;
import com.ironz.binaryprefs.impl.TestUser;
import com.ironz.binaryprefs.fetch.FetchStrategy;
import com.ironz.binaryprefs.fetch.LazyFetchStrategy;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.lock.SimpleLockFactory;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import com.ironz.binaryprefs.task.TaskExecutor;
import com.ironz.binaryprefs.task.TestTaskExecutor;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Map;
import java.util.Set;
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
        Map<String, Set<String>> allCacheCandidates = new ConcurrentHashMap<>();
        Map<String, Map<String, Object>> allCaches = new ConcurrentHashMap<>();

        return create(name, directoryProvider, locks, processLocks, allCacheCandidates, allCaches);
    }

    Preferences create(String name,
                       DirectoryProvider directoryProvider,
                       Map<String, ReadWriteLock> locks,
                       Map<String, Lock> processLocks,
                       Map<String, Set<String>> allCacheCandidates,
                       Map<String, Map<String, Object>> allCaches) {

        final ExceptionHandler exceptionHandler = ExceptionHandler.IGNORE;
        final TaskExecutor taskExecutor = new TestTaskExecutor(exceptionHandler);

        return create(
                name,
                directoryProvider,
                locks,
                processLocks,
                allCacheCandidates,
                allCaches,
                taskExecutor
        );
    }

    Preferences create(String name,
                       DirectoryProvider directoryProvider,
                       Map<String, ReadWriteLock> locks,
                       Map<String, Lock> processLocks,
                       Map<String, Set<String>> allCacheCandidates,
                       Map<String, Map<String, Object>> allCaches,
                       TaskExecutor taskExecutor) {
        FileAdapter fileAdapter = new NioFileAdapter(directoryProvider);
        LockFactory lockFactory = new SimpleLockFactory(name, directoryProvider, locks, processLocks);
        ValueEncryption valueEncryption = new AesValueEncryption("1111111111111111".getBytes(), "0000000000000000".getBytes());
        KeyEncryption keyEncryption = new XorKeyEncryption("1111111111111110".getBytes());
        FileTransaction fileTransaction = new MultiProcessTransaction(fileAdapter, lockFactory, keyEncryption, valueEncryption);
        CacheCandidateProvider candidateProvider = new ConcurrentCacheCandidateProvider(name, allCacheCandidates);
        CacheProvider cacheProvider = new ConcurrentCacheProvider(name, allCaches);
        PersistableRegistry persistableRegistry = new PersistableRegistry();
        persistableRegistry.register(TestUser.KEY, TestUser.class);
        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
        EventBridge eventsBridge = new SimpleEventBridge(name);
        FetchStrategy fetchStrategy = new LazyFetchStrategy(
                lockFactory,
                taskExecutor,
                candidateProvider,
                cacheProvider,
                fileTransaction,
                serializerFactory
        );
        return new BinaryPreferences(
                fileTransaction,
                eventsBridge,
                candidateProvider,
                cacheProvider,
                taskExecutor,
                serializerFactory,
                lockFactory,
                fetchStrategy
        );
    }
}