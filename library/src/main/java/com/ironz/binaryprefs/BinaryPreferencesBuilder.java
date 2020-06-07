package com.ironz.binaryprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;

import com.ironz.binaryprefs.cache.candidates.CacheCandidateProvider;
import com.ironz.binaryprefs.cache.candidates.ConcurrentCacheCandidateProvider;
import com.ironz.binaryprefs.cache.provider.CacheProvider;
import com.ironz.binaryprefs.cache.provider.ConcurrentCacheProvider;
import com.ironz.binaryprefs.encryption.KeyEncryption;
import com.ironz.binaryprefs.encryption.ValueEncryption;
import com.ironz.binaryprefs.event.BroadcastEventBridge;
import com.ironz.binaryprefs.event.EventBridge;
import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.event.MainThreadEventBridge;
import com.ironz.binaryprefs.exception.PreferencesInitializationException;
import com.ironz.binaryprefs.fetch.EagerFetchStrategy;
import com.ironz.binaryprefs.fetch.FetchStrategy;
import com.ironz.binaryprefs.fetch.LazyFetchStrategy;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.file.adapter.NioFileAdapter;
import com.ironz.binaryprefs.file.directory.AndroidDirectoryProvider;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.file.transaction.FileTransaction;
import com.ironz.binaryprefs.file.transaction.MultiProcessTransaction;
import com.ironz.binaryprefs.lock.LockFactory;
import com.ironz.binaryprefs.lock.SimpleLockFactory;
import com.ironz.binaryprefs.migration.MigrateProcessor;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.serialization.serializer.persistable.Persistable;
import com.ironz.binaryprefs.serialization.serializer.persistable.PersistableRegistry;
import com.ironz.binaryprefs.task.ScheduledBackgroundTaskExecutor;
import com.ironz.binaryprefs.task.TaskExecutor;
import com.ironz.binaryprefs.task.barrierprovider.FutureBarrierProvider;
import com.ironz.binaryprefs.task.barrierprovider.impl.InterruptableFutureBarrierProvider;
import com.ironz.binaryprefs.task.barrierprovider.impl.UnInterruptableFutureBarrierProvider;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Class for building preferences instance.
 */
@SuppressWarnings("unused")
public final class BinaryPreferencesBuilder {

    /**
     * Default name of preferences which name has not been defined.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_NAME = "default";
    private static final String INCORRECT_THREAD_INIT_MESSAGE = "Preferences should be instantiated in the main thread.";
    private final ParametersProvider parametersProvider = new ParametersProvider();

    private final Map<String, ReadWriteLock> locks = parametersProvider.getLocks();
    private final Map<String, Lock> processLocks = parametersProvider.getProcessLocks();
    private final Map<String, ExecutorService> executors = parametersProvider.getExecutors();
    private final Map<String, Map<String, Object>> caches = parametersProvider.getCaches();
    private final Map<String, Set<String>> cacheCandidates = parametersProvider.getCacheCandidates();
    private final Map<String, List<SharedPreferences.OnSharedPreferenceChangeListener>> allListeners = parametersProvider.getAllListeners();

    private final Context context;
    private final PersistableRegistry persistableRegistry = new PersistableRegistry();
    private final MigrateProcessor migrateProcessor = new MigrateProcessor();

    private File baseDir;
    private String name = DEFAULT_NAME;
    private boolean supportInterProcess = false;
    private boolean allowBuildOnBackgroundThread = false;
    private MemoryCacheMode memoryCacheMode = MemoryCacheMode.LAZY;
    private KeyEncryption keyEncryption = KeyEncryption.NO_OP;
    private ValueEncryption valueEncryption = ValueEncryption.NO_OP;
    private ExceptionHandler exceptionHandler = ExceptionHandler.PRINT;
    private TaskExecutorMode taskExecutorMode = TaskExecutorMode.NON_INTERRUPTIBLE;

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
     * Be careful: write into external directory required appropriate
     * runtime and manifest permissions.
     *
     * @param baseDir base directory for saving.
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
     * Defines in-memory cache fetching strategy.
     * Default value is {@code true}.
     *
     * @param mode required memory cache mode
     * @return current builder instance
     */
    public BinaryPreferencesBuilder memoryCacheMode(MemoryCacheMode mode) {
        this.memoryCacheMode = mode;
        return this;
    }

    /**
     * Defines key encryption implementation which performs vice versa byte encryption operations.
     * Default value is {@link KeyEncryption#NO_OP}
     *
     * @param keyEncryption key encryption implementation
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
     * @param valueEncryption value encryption implementation
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
     * @param executorMode required execution mode.
     *                     Default value is {@link TaskExecutorMode#NON_INTERRUPTIBLE}
     * @return current builder instance
     */
    public BinaryPreferencesBuilder executorMode(TaskExecutorMode executorMode) {
        this.taskExecutorMode = executorMode;
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
     * Allows to build instance of {@link BinaryPreferences} on background thread.
     * <p>
     * <b>WARNING:</b> instantiating preferences concurrently can
     * lead to in-memory cache race conditions.
     * Be sure that migration performs not in the parallel.
     *
     * @return current builder instance
     */
    public BinaryPreferencesBuilder allowBuildOnBackgroundThread() {
        allowBuildOnBackgroundThread = true;
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
        if (!allowBuildOnBackgroundThread && Looper.myLooper() != Looper.getMainLooper()) {
            throw new PreferencesInitializationException(INCORRECT_THREAD_INIT_MESSAGE);
        }
        BinaryPreferences preferences = createInstance();
        migrateProcessor.migrateTo(preferences);
        return preferences;
    }

    private BinaryPreferences createInstance() {

        DirectoryProvider directoryProvider = new AndroidDirectoryProvider(name, baseDir);
        FileAdapter fileAdapter = new NioFileAdapter(directoryProvider);
        LockFactory lockFactory = new SimpleLockFactory(name, directoryProvider, locks, processLocks);
        FileTransaction fileTransaction = new MultiProcessTransaction(fileAdapter, lockFactory, keyEncryption, valueEncryption);
        CacheCandidateProvider cacheCandidateProvider = new ConcurrentCacheCandidateProvider(name, cacheCandidates);
        CacheProvider cacheProvider = new ConcurrentCacheProvider(name, caches);

        FutureBarrierProvider futureBarrierProvider = taskExecutorMode == TaskExecutorMode.INTERRUPTIBLE
                ? new InterruptableFutureBarrierProvider()
                : new UnInterruptableFutureBarrierProvider();
        TaskExecutor taskExecutor = new ScheduledBackgroundTaskExecutor(name, exceptionHandler, executors, futureBarrierProvider);

        SerializerFactory serializerFactory = new SerializerFactory(persistableRegistry);
        EventBridge eventsBridge = supportInterProcess ? new BroadcastEventBridge(
                context,
                name,
                cacheCandidateProvider,
                cacheProvider,
                serializerFactory,
                taskExecutor,
                valueEncryption,
                directoryProvider,
                allListeners
        ) : new MainThreadEventBridge(name, allListeners);

        FetchStrategy strategy = memoryCacheMode == MemoryCacheMode.LAZY ? new LazyFetchStrategy(
                lockFactory,
                taskExecutor,
                cacheCandidateProvider,
                cacheProvider,
                fileTransaction,
                serializerFactory
        ) : new EagerFetchStrategy(
                lockFactory,
                taskExecutor,
                cacheCandidateProvider,
                cacheProvider,
                fileTransaction,
                serializerFactory
        );

        return new BinaryPreferences(
                fileTransaction,
                eventsBridge,
                cacheCandidateProvider,
                cacheProvider,
                taskExecutor,
                serializerFactory,
                lockFactory,
                strategy
        );
    }

    /**
     * Defines target mode for various in-memory cache fill scenario
     */
    public enum MemoryCacheMode {
        /**
         * Fill cache value only after request, e.g. just in time
         */
        LAZY,
        /**
         * Fill cache immediately after preferences initialization
         */
        EAGER
    }

    /**
     * Defines mode for proper handling while thread is interrupted, before this settings was {@link TaskExecutorMode#INTERRUPTIBLE}
     */
    public enum TaskExecutorMode {
        INTERRUPTIBLE,
        NON_INTERRUPTIBLE
    }

}
