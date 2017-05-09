package com.ironz.binaryprefs.file;

import com.ironz.binaryprefs.task.TaskExecutor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

@SuppressWarnings("FieldCanBeLocal")
public class NioFileAdapterTest {

    private final String fileName = "file.name";
    private final String fileNameTwo = fileName + 2;
    private final byte[] bytes = "value".getBytes();
    private final byte[] bytesTwo = "eulav123".getBytes();

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();
    private FileAdapter fileAdapter;
    private File srcDir;

    @Before
    public void setUp() throws Exception {
        srcDir = folder.newFolder();
        fileAdapter = new NioFileAdapter(new DirectoryProvider() {
            @Override
            public File getBaseDirectory() {
                return srcDir;
            }
        });
    }

    @Test
    public void directoryProvider() {
        NioFileAdapter nioFileAdapter = new NioFileAdapter(new DirectoryProvider() {
            @Override
            public File getBaseDirectory() {
                return srcDir;
            }
        }, TaskExecutor.DEFAULT);
        assertEquals(srcDir, nioFileAdapter.srcDir);
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

        fileAdapter.clear();

        assertFalse(fileAdapter.contains(fileName));
        assertFalse(fileAdapter.contains(fileNameTwo));
    }
}