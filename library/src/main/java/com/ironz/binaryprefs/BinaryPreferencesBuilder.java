package com.ironz.binaryprefs;

import android.content.Context;

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
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import com.ironz.binaryprefs.task.ScheduledBackgroundTaskExecutor;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.HashMap;
import java.util.Map;

public class BinaryPreferencesBuilder {

    private static final String DEFAULT_NAME = "default";
    private final Context context;
    private final Map<String, Class<? extends Persistable>> persistableByToken = new HashMap<>();
    private byte[] secret;
    private byte[] initialVector;
    private String name = DEFAULT_NAME;
    private ExceptionHandler exceptionHandler = ExceptionHandler.IGNORE;

    public BinaryPreferencesBuilder(Context context) {
        this.context = context;
    }

    public BinaryPreferencesBuilder encryption(byte[] secret, byte[] initialVector) {
        this.secret = secret;
        this.initialVector = initialVector;
        return this;
    }

    public BinaryPreferencesBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public BinaryPreferencesBuilder name(String name) {
        this.name = name;
        return this;
    }

    public BinaryPreferencesBuilder registerPersistableToken(String token, Class<? extends Persistable> persistable) {
        persistableByToken.put(token, persistable);
        return this;
    }

    public BinaryPreferencesBuilder registerPersistableTokens(Map<String, Class<? extends Persistable>> persistableByToken) {
        this.persistableByToken.putAll(persistableByToken);
        return this;
    }

    public final Preferences build() {
        DirectoryProvider directoryProvider = new AndroidDirectoryProviderImpl(context, name);
        FileAdapter fileAdapter = new NioFileAdapter(directoryProvider);
        LockFactory lockFactory = new SimpleLockFactoryImpl(name, directoryProvider);
        FileTransaction fileTransaction = new MultiProcessTransactionImpl(fileAdapter, lockFactory);
        ByteEncryption byteEncryption = getByteEncryption();
        CacheProvider cacheProvider = new ConcurrentCacheProviderImpl(name);
        TaskExecutor executor = new ScheduledBackgroundTaskExecutor(exceptionHandler);
        PersistableRegistry persistableRegistry = new PersistableRegistry();
        if (!persistableByToken.isEmpty()) {
            for (Map.Entry<String, Class<? extends Persistable>> entry : persistableByToken.entrySet()) {
                persistableRegistry.register(entry.getKey(), entry.getValue());
            }
        }
        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
        EventBridge eventsBridge = new BroadcastEventBridgeImpl(context, name, cacheProvider, serializerFactory, executor, byteEncryption);
        return new BinaryPreferences(fileTransaction, byteEncryption, eventsBridge, cacheProvider, executor, serializerFactory, lockFactory);
    }

    private ByteEncryption getByteEncryption() {
        if (secret != null && initialVector != null) {
            return new AesByteEncryptionImpl(secret, initialVector);
        } else {
            return ByteEncryption.NO_OP;
        }
    }
}
