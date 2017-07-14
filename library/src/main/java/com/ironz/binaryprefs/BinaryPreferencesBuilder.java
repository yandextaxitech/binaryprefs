package com.ironz.binaryprefs;

import android.content.Context;
import android.os.Looper;
import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.cache.ConcurrentCacheProviderImpl;
import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.events.BroadcastEventBridgeImpl;
import com.ironz.binaryprefs.events.EventBridge;
import com.ironz.binaryprefs.events.MainThreadEventBridgeImpl;
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

/**
 * Describes public api for building preferences instance.
 */
@SuppressWarnings("unused")
public final class BinaryPreferencesBuilder {

    /**
     * Default name of preferences which name has not been defined.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_NAME = "default";

    private final Context context;
    private final PersistableRegistry persistableRegistry = new PersistableRegistry();

    private String name = DEFAULT_NAME;
    private boolean externalStorage = false;
    private boolean supportInterProcess = false;
    private ByteEncryption byteEncryption = ByteEncryption.NO_OP;
    private ExceptionHandler exceptionHandler = ExceptionHandler.PRINT;

    /**
     * Creates builder with base parameters.
     * <p>
     * Note: Please, use only one instance of preferences by name,
     * this saves you from non-reasoned allocations.
     * </p>
     *
     * @param context target context. Using application context is
     *                very much appreciated
     */
    public BinaryPreferencesBuilder(Context context) {
        this.context = context;
    }

    /**
     * Defines preferences name for build instance.
     *
     * @param name target preferences name. Default name is {@link #DEFAULT_NAME}
     * @return current builder instance
     */
    public BinaryPreferencesBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Defines usage of external directory for preferences saving.
     * Default value is {@code false}.
     *
     * @param value {@code true} if would use external storage, {@code false} otherwise
     * @return current builder instance
     */
    public BinaryPreferencesBuilder externalStorage(boolean value) {
        this.externalStorage = value;
        return this;
    }

    /**
     * Defines usage of IPC mechanism for delivering key updates and cache consistency.
     * Default value is {@code false}.
     * <p>
     * Note: Please, note that one key change delta should be less than 1 (one) megabyte
     * because IPC data transferring is limited by this capacity.
     * </p>
     *
     * @param value {@code true} if would use IPC, {@code false} otherwise
     * @return current builder instance
     */
    public BinaryPreferencesBuilder supportInterProcess(boolean value) {
        this.supportInterProcess = value;
        return this;
    }

    /**
     * Defines encryption implementation which performs vice-versa byte encryption operations.
     * Default value is {@link ByteEncryption#NO_OP}
     *
     * @param byteEncryption byte encryption implementation
     * @return current builder instance
     */
    public BinaryPreferencesBuilder encryption(ByteEncryption byteEncryption) {
        this.byteEncryption = byteEncryption;
        return this;
    }

    /**
     * Defines exception handler implementation which handles exception events e.g. logging operations.
     * Default value is {@link ExceptionHandler#PRINT}
     *
     * @param exceptionHandler exception handler implementation
     * @return current builder instance
     */
    public BinaryPreferencesBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Registers {@link Persistable} data-object for de/serialization process.
     * All {@link Persistable} data-objects should be registered for understanding
     * de/serialization contract during cache initialization.
     *
     * @param key         target key which uses for fetching {@link Persistable}
     *                    in {@link PreferencesEditor#putPersistable(String, Persistable)} method
     * @param persistable target class type which implements {@link Persistable} interface
     * @return current builder instance
     */
    public BinaryPreferencesBuilder registerPersistable(String key, Class<? extends Persistable> persistable) {
        persistableRegistry.register(key, persistable);
        return this;
    }

    /**
     * Builds preferences instance with predefined or default parameters.
     * This method will fails if invocation performed not in the main thread.
     *
     * @return preferences instance with predefined or default parameters.
     * @see PreferencesInitializationException
     */
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


        EventBridge eventsBridge = supportInterProcess ? new BroadcastEventBridgeImpl(
                context,
                name,
                cacheProvider,
                serializerFactory,
                executor,
                byteEncryption
        ) : new MainThreadEventBridgeImpl(name);

        Preferences preferences = new BinaryPreferences(
                fileTransaction,
                byteEncryption,
                eventsBridge,
                cacheProvider,
                executor,
                serializerFactory,
                lockFactory
        );

        // TODO: 7/14/17 redesign this workaround in future
        if (eventsBridge instanceof BroadcastEventBridgeImpl) { //shit happens
            ((BroadcastEventBridgeImpl) eventsBridge).definePreferences(preferences);
        }

        return preferences;
    }
}