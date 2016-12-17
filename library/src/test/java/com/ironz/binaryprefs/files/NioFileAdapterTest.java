package com.ironz.binaryprefs.files;

import com.ironz.binaryprefs.exception.FileOperationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class NioFileAdapterTest {

    private final String name = "file.name";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private FileAdapter fileAdapter;

    @Before
    public void setUp() throws Exception {
        File srcDir = folder.newFolder();
        fileAdapter = new NioFileAdapter(srcDir);
    }

    @Test(expected = FileOperationException.class)
    public void deletedDirectory() {
        folder.delete(); //trying to remove root directory before starting write operation
        fileAdapter.save(name, "value".getBytes());
    }

    @Test
    public void correctSaving() {
        fileAdapter.save(name, "value".getBytes());
    }

    @Test(expected = FileOperationException.class)
    public void missingFile() {
        fileAdapter.fetch(name);
    }

    @Test
    public void correctFile() {
        byte[] bytes = "value".getBytes();
        fileAdapter.save(name, bytes);
        byte[] fetch = fileAdapter.fetch(name);
        assertEquals(new String(bytes), new String(fetch));
    }
}