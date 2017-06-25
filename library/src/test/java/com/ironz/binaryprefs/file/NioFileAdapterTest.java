package com.ironz.binaryprefs.file;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

@SuppressWarnings("FieldCanBeLocal")
public class NioFileAdapterTest {

    private static final String FILE_NAME = "file.name";
    private final byte[] bytes = "value".getBytes();
    private final byte[] bytesTwo = "eulav123".getBytes();

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private String fileName;
    private String fileNameTwo;

    private FileAdapter fileAdapter;
    private File srcDir;

    @Before
    public void setUp() throws Exception {

        srcDir = folder.newFolder();
        File fileOne = new File(srcDir, FILE_NAME);
        fileName = fileOne.getAbsolutePath();
        File fileTwo = new File(srcDir, FILE_NAME + "2");
        fileNameTwo = fileTwo.getAbsolutePath();

        fileAdapter = new NioFileAdapter();
    }

    @Test
    public void saving() {
        fileAdapter.save(fileName, bytes);
    }

    @Test
    public void restore() {

        fileAdapter.save(fileName, bytes);
        byte[] fetch = fileAdapter.fetch(fileName);

        assertEquals(new String(bytes), new String(fetch));
    }

    @Test
    public void restoreShorter() {

        fileAdapter.save(fileName, bytesTwo);
        fileAdapter.save(fileName, bytes);
        byte[] fetch = fileAdapter.fetch(fileName);

        assertEquals(new String(bytes), new String(fetch));
    }

    @Test
    public void restoreLonger() {

        fileAdapter.save(fileName, bytes);
        fileAdapter.save(fileName, bytesTwo);
        byte[] fetch = fileAdapter.fetch(fileName);

        assertEquals(new String(bytesTwo), new String(fetch));
    }

    @Test
    public void deleteOne() {
        fileAdapter.save(fileName, bytes);
        assertTrue(fileAdapter.contains(fileName));

        fileAdapter.remove(fileName);
        assertFalse(fileAdapter.contains(fileName));
    }

    @Test
    public void deleteAll() {
        fileAdapter.save(fileName, bytes);
        fileAdapter.save(fileNameTwo, bytes);

        assertTrue(fileAdapter.contains(fileName));
        assertTrue(fileAdapter.contains(fileNameTwo));

        fileAdapter.remove(fileName);
        fileAdapter.remove(fileNameTwo);

        assertFalse(fileAdapter.contains(fileName));
        assertFalse(fileAdapter.contains(fileNameTwo));
    }
}