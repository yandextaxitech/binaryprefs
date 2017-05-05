package com.ironz.binaryprefs.file;

/**
 * Adapter abstraction which describes a file operation contract.
 * It's used for making unique implementation for concrete file operations.
 */
public interface FileAdapter {

    /**
     * Returns all names for directory.
     *
     * @return file names with extension suffix
     */
    String[] names();

    /**
     * Returns byte array for concrete file by name or empty byte array if exception throws.
     *
     * @param name file name with extension
     * @return byte array of file
     */
    byte[] fetch(String name);

    /**
     * Saves byte array to concrete file, if file exists it will be overwritten.
     *
     * @param name  file name with extension
     * @param bytes byte array for saving
     */
    void save(String name, byte[] bytes);

    /**
     * Deletes all files from concrete directory
     */
    void clear();

    /**
     * Removes file by key.
     *
     * @param name file name with extension
     */
    void remove(String name);

    /**
     * Returns {@code true} if file exists {@code false} otherwise.
     *
     * @param name exact name pattern
     * @return true if FS contains false otherwise
     */
    boolean contains(String name);

    /**
     * Clears cache for all fetched elements
     */
    void evictCache();
}