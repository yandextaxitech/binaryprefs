package com.ironz.binaryprefs.file;

import com.ironz.binaryprefs.exception.FileOperationException;
import com.ironz.binaryprefs.file.adapter.FileAdapter;
import com.ironz.binaryprefs.file.adapter.NioFileAdapter;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("FieldCanBeLocal")
public final class NioFileAdapterTest {

    private static final String FILE_NAME = "file.name";
    private static final String FILE_NAME_1 = "file.name.1";
    private final byte[] bytes = "value".getBytes();
    private final byte[] bytesTwo = "eulav123".getBytes();

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private FileAdapter fileAdapter;

    @Before
    public void setUp() throws Exception {
        final File srcDir = folder.newFolder("preferences");
        final File backupDir = folder.newFolder("backup");
        final File lockDir = folder.newFolder("lock");
        DirectoryProvider directoryProvider = new DirectoryProvider() {
            @Override
            public File getStoreDirectory() {
                return srcDir;
            }

            @Override
            public File getBackupDirectory() {
                return backupDir;
            }

            @Override
            public File getLockDirectory() {
                return lockDir;
            }
        };
        fileAdapter = new NioFileAdapter(directoryProvider);
    }

    @Test
    public void saving() {
        fileAdapter.save(FILE_NAME, bytes);
    }

    @Test(expected = FileOperationException.class)
    public void savingEmptyBytes() {
        fileAdapter.save(FILE_NAME, new byte[0]);
    }

    @Test
    public void restore() {
        fileAdapter.save(FILE_NAME, bytes);

        byte[] fetch = fileAdapter.fetch(FILE_NAME);

        assertEquals(new String(bytes), new String(fetch));
    }

    @Test
    public void restoreShorter() {
        fileAdapter.save(FILE_NAME, bytesTwo);
        fileAdapter.save(FILE_NAME, bytes);

        byte[] fetch = fileAdapter.fetch(FILE_NAME);

        assertEquals(new String(bytes), new String(fetch));
    }

    @Test
    public void restoreLonger() {
        fileAdapter.save(FILE_NAME, bytes);
        fileAdapter.save(FILE_NAME, bytesTwo);

        byte[] fetch = fileAdapter.fetch(FILE_NAME);

        assertEquals(new String(bytesTwo), new String(fetch));
    }

    @Test(expected = FileOperationException.class)
    public void deleteOne() {
        fileAdapter.save(FILE_NAME, bytes);

        assertNotNull(fileAdapter.fetch(FILE_NAME));

        fileAdapter.remove(FILE_NAME);
        fileAdapter.fetch(FILE_NAME);
    }

    @Test(expected = FileOperationException.class)
    public void deleteAll() {
        fileAdapter.save(FILE_NAME, bytes);
        fileAdapter.save(FILE_NAME_1, bytes);

        assertNotNull(fileAdapter.fetch(FILE_NAME));
        assertNotNull(fileAdapter.fetch(FILE_NAME_1));

        fileAdapter.remove(FILE_NAME_1);

        assertNotNull(fileAdapter.fetch(FILE_NAME));
        fileAdapter.fetch(FILE_NAME_1);
    }
}