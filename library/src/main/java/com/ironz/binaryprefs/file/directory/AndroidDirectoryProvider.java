package com.ironz.binaryprefs.file.directory;

import com.ironz.binaryprefs.exception.FileOperationException;

import java.io.File;

/**
 * Provides default android cache directory or external (if possible) cache directory.
 */
public final class AndroidDirectoryProvider implements DirectoryProvider {

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
    public AndroidDirectoryProvider(String prefName, File baseDir) {
        this.storeDirectory = createAndValidate(baseDir, prefName, STORE_DIRECTORY_NAME);
        this.backupDirectory = createAndValidate(baseDir, prefName, BACKUP_DIRECTORY_NAME);
        this.lockDirectory = createAndValidate(baseDir, prefName, LOCK_DIRECTORY_NAME);
    }

    private File createAndValidate(File baseDir, String prefName, String subDirectory) {
        File targetDirectory = create(baseDir, prefName, subDirectory);
        if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
            String absolutePath = targetDirectory.getAbsolutePath();
            throw new FileOperationException(String.format(CANNOT_CREATE_DIR_MESSAGE, absolutePath));
        }
        return targetDirectory;
    }

    private File create(File baseDir, String prefName, String subDirectory) {
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