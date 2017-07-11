package com.ironz.binaryprefs;

import android.content.Context;
import android.os.Looper;
import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.cache.ConcurrentCacheProviderImpl;
import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.events.BroadcastEventBridgeImpl;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.exception.PreferencesInitializationException;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.file.adapter.NioFileAdapter;
import com.ironz.binaryprefs.file.directory.AndroidDirectoryProviderImpl;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.MultiProcessTransactionImpl;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.lock.SimpleLockFactoryImpl;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import com.ironz.binaryprefs.task.ScheduledBackgroundTaskExecutor;
import com.ironz.binaryprefs.task.TaskExecutor;

@SuppressWarnings("unused")
public final class BinaryPreferencesBuilder {

    private static final String DEFAULT_NAME = "default";

    private final Context context;
    private final PersistableRegistry persistableRegistry = new PersistableRegistry();

    private String name = DEFAULT_NAME;
    private boolean externalStorage = false;
    private ByteEncryption byteEncryption = ByteEncryption.NO_OP;
    private ExceptionHandler exceptionHandler = ExceptionHandler.IGNORE;

    public BinaryPreferencesBuilder(Context context) {
        this.context = context;
    }

    public BinaryPreferencesBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BinaryPreferencesBuilder externalStorage(boolean value) {
        this.externalStorage = value;
        return this;
    }

    public BinaryPreferencesBuilder encryption(ByteEncryption byteEncryption) {
        this.byteEncryption = byteEncryption;
        return this;
    }

    public BinaryPreferencesBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public BinaryPreferencesBuilder registerPersistable(String key, Class<? extends Persistable> persistable) {
        persistableRegistry.register(key, persistable);
        return this;
    }

    public Preferences build() {

        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new PreferencesInitializationException("Preferences instantiated not in the main thread.");
        }

        DirectoryProvider directoryProvider = new AndroidDirectoryProviderImpl(context, name, externalStorage);
        FileAdapter fileAdapter = new NioFileAdapter(directoryProvider);
        LockFactory lockFactory = new SimpleLockFactoryImpl(name, directoryProvider);
        FileTransaction fileTransaction = new MultiProcessTransactionImpl(fileAdapter, lockFactory);
        CacheProvider cacheProvider = new ConcurrentCacheProviderImpl(name);
        TaskExecutor executor = new ScheduledBackgroundTaskExecutor(name, exceptionHandler);
        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
        EventBridge eventsBridge = new BroadcastEventBridgeImpl(context, name, cacheProvider, serializerFactory, executor, byteEncryption);
        return new BinaryPreferences(fileTransaction, byteEncryption, eventsBridge, cacheProvider, executor, serializerFactory, lockFactory);
    }
}