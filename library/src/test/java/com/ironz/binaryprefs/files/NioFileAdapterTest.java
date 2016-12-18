package com.ironz.binaryprefs.files;

import com.ironz.binaryprefs.exception.FileOperationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

public class NioFileAdapterTest {

    private final String fileName = "file.name";
    private final byte[] bytes = "value".getBytes();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private FileAdapter fileAdapter;
    private File srcDir;

    @Before
    public void setUp() throws Exception {
        srcDir = folder.newFolder();
        fileAdapter = new NioFileAdapter(srcDir);
    }

    @Test(expected = FileOperationException.class)
    public void deletedDirectory() {
        folder.delete(); //trying to remove root directory before starting write operation
        fileAdapter.save(fileName, bytes);
    }

    @Test
    public void correctSaving() {
        fileAdapter.save(fileName, bytes);
    }

    @Test(expected = FileOperationException.class)
    public void missingFile() {
        fileAdapter.fetch(fileName);
    }

    @Test
    public void correctFile() {
        fileAdapter.save(fileName, bytes);
        byte[] fetch = fileAdapter.fetch(fileName);
        assertEquals(new String(bytes), new String(fetch));
    }

    @Test
    public void deleteOne() {
        File file = new File(srcDir, fileName);
        fileAdapter.save(fileName, bytes);

        assertTrue(file.exists());
        assertFalse(file.isDirectory());

        fileAdapter.remove(fileName);

        assertFalse(file.exists());
    }
}