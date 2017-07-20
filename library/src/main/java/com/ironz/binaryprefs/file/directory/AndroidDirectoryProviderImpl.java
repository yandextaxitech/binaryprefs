package com.ironz.binaryprefs.file.directory;

import com.ironz.binaryprefs.exception.FileOperationException;

import java.io.File;

/**
 * Provides default android cache directory or external (if possible) cache directory.
 */
public final class AndroidDirectoryProviderImpl implements DirectoryProvider {

    private static final String PREFERENCES = "preferences";

    private static final String VALUES = "values";
    private static final String BACKUP = "backup";
    private static final String LOCK = "lock";

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
        storeDirectory = createStoreDirectory(baseDir, prefName, VALUES);
        backupDirectory = createStoreDirectory(baseDir, prefName, BACKUP);
        lockDirectory = createStoreDirectory(baseDir, prefName, LOCK);
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