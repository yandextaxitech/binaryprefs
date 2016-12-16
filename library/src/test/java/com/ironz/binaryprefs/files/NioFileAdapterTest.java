package com.ironz.binaryprefs.files;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;

public class NioFileAdapterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private FileAdapter fileAdapter;

    @Before
    public void setUp() throws Exception {
        File srcDir = folder.newFolder();
        fileAdapter = new NioFileAdapter(srcDir);
    }

    @Test(expected = FileNotFoundException.class)
    public void incorrectDirectory() {
        ((NioFileAdapter) fileAdapter).getSrcDir().delete(); //trying to remove root directory before starting write operation
        fileAdapter.save("file.name", "value".getBytes());
    }
}