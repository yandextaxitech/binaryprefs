package com.ironz.binaryprefs.file.directory;

import com.ironz.binaryprefs.exception.FileOperationException;

import java.io.File;

/**
 * Provides default android cache directory or external (if possible) cache directory.
 */
public final class AndroidDirectoryProviderImpl implements DirectoryProvider {

    private static final String CANNOT_CREATE_DIR_MESSAGE = "Can't create preferences directory in %s";

    static final String PREFERENCES_ROOT_DIRECTORY_NAME = "preferences";

    static final String STORE_DIRECTORY_NAME = "values";
    static final String BACKUP_DIRECTORY_NAME = "backup";
    static final String LOCK_DIRECTORY_NAME = "lock";

    private final File storeDirectory;
    private final File backupDirectory;
    private final File lockDirectory;

    /**
     * Creates instance for default or external (if enabled) persistent cache directory.
     *
     * @param prefName preferences name
     * @param baseDir  all data will be saved inside this directory.
     */
    public AndroidDirectoryProviderImpl(String prefName, File baseDir) {
        storeDirectory = createStoreDirectory(baseDir, prefName, STORE_DIRECTORY_NAME);
        backupDirectory = createStoreDirectory(baseDir, prefName, BACKUP_DIRECTORY_NAME);
        lockDirectory = createStoreDirectory(baseDir, prefName, LOCK_DIRECTORY_NAME);
    }

    private File createStoreDirectory(File baseDir, String prefName, String subDirectory) {
        File targetDirectory = createTargetDirectory(baseDir, prefName, subDirectory);
        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            throw new FileOperationException(String.format(CANNOT_CREATE_DIR_MESSAGE, targetDirectory));
        }
        return targetDirectory;
    }

    private File createTargetDirectory(File baseDir, String prefName, String subDirectory) {
        File prefsDir = new File(baseDir, PREFERENCES_ROOT_DIRECTORY_NAME);
        File prefNameDir = new File(prefsDir, prefName);
        return new File(prefNameDir, subDirectory);
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