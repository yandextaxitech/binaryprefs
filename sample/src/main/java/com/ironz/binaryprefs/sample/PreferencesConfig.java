package com.ironz.binaryprefs.sample;

import android.content.Context;
import com.ironz.binaryprefs.BinaryPreferences;
import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.cache.ConcurrentCacheProviderImpl;
import com.ironz.binaryprefs.encryption.AesByteEncryptionImpl;
import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.events.BroadcastEventBridgeImpl;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.file.adapter.NioFileAdapter;
import com.ironz.binaryprefs.file.directory.AndroidDirectoryProviderImpl;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.MultiProcessTransactionImpl;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.lock.SimpleLockFactoryImpl;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import com.ironz.binaryprefs.task.ScheduledBackgroundTaskExecutor;
import com.ironz.binaryprefs.task.TaskExecutor;

final class PreferencesConfig {

    BinaryPreferences createBinaryPreferences(Context context) {
        String name = "preferences";
        DirectoryProvider directoryProvider = new AndroidDirectoryProviderImpl(context, name);
        FileAdapter fileAdapter = new NioFileAdapter(directoryProvider);
        ExceptionHandler exceptionHandler = new ExceptionHandler() {
            @Override
            public void handle(Exception e) {
                e.printStackTrace();
            }
        };
        LockFactory lockFactory = new SimpleLockFactoryImpl(name, directoryProvider);
        FileTransaction fileTransaction = new MultiProcessTransactionImpl(fileAdapter, lockFactory);
        ByteEncryption byteEncryption = new AesByteEncryptionImpl("1111111111111111".getBytes(), "0000000000000000".getBytes());
        CacheProvider cacheProvider = new ConcurrentCacheProviderImpl(name);
        TaskExecutor taskExecutor = new ScheduledBackgroundTaskExecutor(exceptionHandler);
        PersistableRegistry persistableRegistry = new PersistableRegistry();
        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
        EventBridge eventsBridge = new BroadcastEventBridgeImpl(context, name, cacheProvider, serializerFactory, taskExecutor, byteEncryption);

        return new BinaryPreferences(
                fileTransaction,
                byteEncryption,
                eventsBridge,
                cacheProvider,
                taskExecutor,
                serializerFactory,
                lockFactory
        );
    }
}