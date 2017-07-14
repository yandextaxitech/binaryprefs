package com.ironz.binaryprefs.file.directory;

import android.content.Context;
import com.ironz.binaryprefs.exception.FileOperationException;

import java.io.File;

/**
 * Provides default android cache directory or external (if possible) cache directory.
 */
@SuppressWarnings("unused")
public final class AndroidDirectoryProviderImpl implements DirectoryProvider {

    private static final String PREFERENCES = "preferences";
    private static final String VALUES = "values";
    private static final String BACKUP = "backup";
    private static final String LOCK = "lock";

    private final File storeDirectory;
    private final File backupDirectory;
    private final File lockDirectory;

    /**
     * Creates instance for default app cache directory.
     *
     * @param context  target app context
     * @param prefName preferences name
     */
    @SuppressWarnings("unused")
    public AndroidDirectoryProviderImpl(Context context, String prefName) {
        this(context, prefName, false);
    }

    /**
     * Creates instance for default or external (if enabled) persistent cache directory.
     *
     * @param context         target app context
     * @param prefName        preferences name
     * @param externalStorage all data will be saved inside external cache directory
     *                        if <code>true</code> value is passed
     *                        ({@link Context#getExternalCacheDir()}),
     *                        if <code>false</code> - will use standard app cache directory
     *                        ({@link Context#getCacheDir()}).
     */
    @SuppressWarnings("WeakerAccess")
    public AndroidDirectoryProviderImpl(Context context, String prefName, boolean externalStorage) {
        File baseDir = defineCacheDir(context, externalStorage);
        storeDirectory = createStoreDirectory(baseDir, prefName, VALUES);
        backupDirectory = createStoreDirectory(baseDir, prefName, BACKUP);
        lockDirectory = createStoreDirectory(baseDir, prefName, LOCK);
    }

    private File defineCacheDir(Context context, boolean saveInExternal) {
        return saveInExternal ? context.getExternalCacheDir() : context.getCacheDir();
    }

    private File createStoreDirectory(File baseDir, String prefName, String subDirectory) {
        File prefsDir = new File(baseDir, PREFERENCES);
        File prefNameDir = new File(prefsDir, prefName);
        File targetDirectory = new File(prefNameDir, subDirectory);
        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            throw new FileOperationException(String.format("Cannot create preferences directory in %s", targetDirectory.getAbsolutePath()));
        }
        return targetDirectory;
    }

    @Override
    public File getStoreDirectory() {
        return storeDirectory;
    }

    @Override
    public File getBackupDirectory() {
        return backupDirectory;
    }

    @Override
    public File getLockDirectory() {
        return lockDirectory;
    }
}