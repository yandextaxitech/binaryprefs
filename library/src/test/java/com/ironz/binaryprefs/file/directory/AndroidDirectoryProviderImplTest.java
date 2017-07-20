package com.ironz.binaryprefs.file.directory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class AndroidDirectoryProviderImplTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();
    private File baseDir;


    @Before
    public void setUp() throws Exception {
        baseDir = folder.newFolder();
    }

    @Test
    public void path() {
        DirectoryProvider provider = new AndroidDirectoryProviderImpl("user_data", baseDir);
        provider.getBackupDirectory();
    }
}