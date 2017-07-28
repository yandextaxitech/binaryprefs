package com.ironz.binaryprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.cache.ConcurrentCacheProviderImpl;
import com.ironz.binaryprefs.encryption.KeyEncryption;
import com.ironz.binaryprefs.encryption.ValueEncryption;
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
import com.ironz.binaryprefs.migration.MigrateProcessor;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import com.ironz.binaryprefs.task.ScheduledBackgroundTaskExecutor;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.io.File;

/**
 * Class for building preferences instance.
 */
@SuppressWarnings("unused")
public final class BinaryPreferencesBuilder {

    private static final String INCORRECT_THREAD_INIT_MESSAGE = "Preferences should be instantiated in the main thread.";

    /**
     * Default name of preferences which name has not been defined.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_NAME = "default";

    private final Context context;
    private final PersistableRegistry persistableRegistry = new PersistableRegistry();
    private final MigrateProcessor migrateProcessor = new MigrateProcessor();

    private File baseDir;
    private String name = DEFAULT_NAME;
    private boolean supportInterProcess = false;
    private KeyEncryption keyEncryption = KeyEncryption.NO_OP;
    private ValueEncryption valueEncryption = ValueEncryption.NO_OP;
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
        this.baseDir = context.getFilesDir();
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
     * @param value all data will be saved inside external cache directory
     *              if <code>true</code> value is passed
     *              ({@link Context#getExternalFilesDir(String)}),
     *              if <code>false</code> - will use standard app cache directory
     *              ({@link Context#getFilesDir()}).
     * @return current builder instance
     */
    public BinaryPreferencesBuilder externalStorage(boolean value) {
        this.baseDir = value ? context.getExternalFilesDir(null) : context.getFilesDir();
        return this;
    }

    /**
     * * Defines usage of custom directory for preferences saving.
     *
     * @param baseDir base directory for saving.
     *                This is useful for data restoring after
     * @return current builder instance
     */
    public BinaryPreferencesBuilder customDirectory(File baseDir) {
        this.baseDir = baseDir;
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
     * Defines key encryption implementation which performs vice versa byte encryption operations.
     * Default value is {@link KeyEncryption#NO_OP}
     *
     * @param keyEncryption keyEncryption implementation
     * @return current builder instance
     */
    public BinaryPreferencesBuilder keyEncryption(KeyEncryption keyEncryption) {
        this.keyEncryption = keyEncryption;
        return this;
    }

    /**
     * Defines value encryption implementation which performs vice versa byte encryption operations.
     * Default value is {@link ValueEncryption#NO_OP}
     *
     * @param valueEncryption byte encryption implementation
     * @return current builder instance
     */
    public BinaryPreferencesBuilder valueEncryption(ValueEncryption valueEncryption) {
        this.valueEncryption = valueEncryption;
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
     * Performs migration from any implementation of preferences
     * to this implementation.
     * Appropriate transaction will be created for all migrated
     * values. After successful migration all data in migrated
     * preferences will be removed.
     * Please note that all existing values in this implementation
     * will be rewritten to values which migrates into. Also type
     * information will be rewritten and lost too without any
     * exception.
     * If this method will be called multiple times for two or more
     * different instances of preferences which has keys collision
     * then last preferences values will be applied.
     *
     * @param preferences any implementation for migration.
     * @return current builder instance
     */
    public BinaryPreferencesBuilder migrateFrom(SharedPreferences preferences) {
        migrateProcessor.add(preferences);
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
            throw new PreferencesInitializationException(INCORRECT_THREAD_INIT_MESSAGE);
        }

        BinaryPreferences preferences = createInstance();

        migrateProcessor.migrateTo(preferences);

        return preferences;
    }

    private BinaryPreferences createInstance() {
        DirectoryProvider directoryProvider = new AndroidDirectoryProviderImpl(name, baseDir);
        FileAdapter fileAdapter = new NioFileAdapter(directoryProvider);
        LockFactory lockFactory = new SimpleLockFactoryImpl(name, directoryProvider);
        FileTransaction fileTransaction = new MultiProcessTransactionImpl(fileAdapter, lockFactory, valueEncryption, keyEncryption);
        CacheProvider cacheProvider = new ConcurrentCacheProviderImpl(name);
        TaskExecutor executor = new ScheduledBackgroundTaskExecutor(name, exceptionHandler);
        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);

        EventBridge eventsBridge = supportInterProcess ? new BroadcastEventBridgeImpl(
                context,
                name,
                cacheProvider,
                serializerFactory,
                executor,
                valueEncryption
        ) : new MainThreadEventBridgeImpl(name);

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