package com.ironz.binaryprefs.file.directory;

import java.io.File;

/**
 * Define directories for backup, storing, and locking key/values
 */
public interface DirectoryProvider {
    /**
     * Define concrete store directory
     *
     * @return Concrete store directory which will store value files by key
     */
    File getStoreDirectory();

    /**
     * Define concrete backup directory
     *
     * @return Concrete backup directory which will store backup value files by key
     */
    File getBackupDirectory();

    /**
     * Define concrete lock directory
     *
     * @return Concrete lock directory which will store lock files by key
     */
    File getLockDirectory();
}
