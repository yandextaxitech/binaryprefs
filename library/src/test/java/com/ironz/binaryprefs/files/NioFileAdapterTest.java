package com.ironz.binaryprefs.files;

import com.ironz.binaryprefs.exception.FileOperationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class NioFileAdapterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private FileAdapter fileAdapter;

    @Before
    public void setUp() throws Exception {
        File srcDir = folder.newFolder();
        fileAdapter = new NioFileAdapter(srcDir);
    }

    @Test(expected = FileOperationException.class)
    public void incorrectDirectory() {
        folder.delete(); //trying to remove root directory before starting write operation
        fileAdapter.save("file.name", "value".getBytes());
    }

    @Test
    public void correctSaving() {
        fileAdapter.save("file.name", "value".getBytes());
    }
}