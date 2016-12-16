package com.ironz.binaryprefs.files;

import org.junit.Before;
import org.junit.Rule;
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
}