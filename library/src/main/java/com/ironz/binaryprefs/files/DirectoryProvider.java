package com.ironz.binaryprefs.files;

import java.io.File;

/**
 * Define directory for storing key/values
 */
public interface DirectoryProvider {
    /**
     * Define concrete storing directory
     *
     * @return Concrete storing directory which will store value files by key separately
     */
    File getBaseDirectory();
}
