package com.ironz.binaryprefs.file.directory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class AndroidDirectoryProviderImplTest {

    private static final String PREF_NAME = "user_data";

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private File baseDir;
    private DirectoryProvider provider;

    @Before
    public void setUp() throws Exception {
        baseDir = folder.newFolder("preferences");
        provider = new AndroidDirectoryProviderImpl(PREF_NAME, baseDir);
    }

    @Test
    public void lockPath() {

        File lockDirectory = provider.getLockDirectory();

        assertTrue(lockDirectory.exists());
        assertTrue(lockDirectory.canRead());
        assertTrue(lockDirectory.canWrite());

        String actualPath = lockDirectory.getAbsolutePath();

        String expectedPath = baseDir.getAbsolutePath() + File.separator
                + AndroidDirectoryProviderImpl.PREFERENCES_ROOT_DIRECTORY_NAME + File.separator
                + PREF_NAME + File.separator
                + AndroidDirectoryProviderImpl.LOCK_DIRECTORY_NAME;

        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void storePath() {

        File storeDirectory = provider.getStoreDirectory();

        assertTrue(storeDirectory.exists());
        assertTrue(storeDirectory.canRead());
        assertTrue(storeDirectory.canWrite());

        String actualPath = storeDirectory.getAbsolutePath();

        String expectedPath = baseDir.getAbsolutePath() + File.separator
                + AndroidDirectoryProviderImpl.PREFERENCES_ROOT_DIRECTORY_NAME + File.separator
                + PREF_NAME + File.separator
                + AndroidDirectoryProviderImpl.STORE_DIRECTORY_NAME;

        assertEquals(expectedPath, actualPath);
    }

    @Test
    public void backupPath() {

        File backupDirectory = provider.getBackupDirectory();

        assertTrue(backupDirectory.exists());
        assertTrue(backupDirectory.canRead());
        assertTrue(backupDirectory.canWrite());

        String actualPath = backupDirectory.getAbsolutePath();

        String expectedPath = baseDir.getAbsolutePath() + File.separator
                + AndroidDirectoryProviderImpl.PREFERENCES_ROOT_DIRECTORY_NAME + File.separator
                + PREF_NAME + File.separator
                + AndroidDirectoryProviderImpl.BACKUP_DIRECTORY_NAME;

        assertEquals(expectedPath, actualPath);
    }
}