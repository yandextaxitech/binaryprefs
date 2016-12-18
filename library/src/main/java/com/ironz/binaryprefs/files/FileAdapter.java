package com.ironz.binaryprefs.files;

/**
 * Adapter abstraction which describes a file operation contract.
 * It's used for making unique implementation for concrete file operations.
 */
public interface FileAdapter {

    /**
     * Returns all names for directory
     *
     * @return file names with extension suffix
     */
    String[] names();

    /**
     * Returns byte array for concrete file by name or empty byte array if exception throws
     *
     * @param name file name with extension
     * @return byte array of file
     */
    byte[] fetch(String name);

    /**
     * Save byte array to concrete file, if file exists it will be overwritten
     *
     * @param name  file name with extension
     * @param bytes byte array for saving
     */
    void save(String name, byte[] bytes);

    /**
     * Delete all files from concrete directory
     *
     * @return all files has been removed
     */
    void clear();

    /**
     * Removes file by key
     *
     * @param name file name with extension
     * @return value has been removed
     */
    void remove(String name);
}
