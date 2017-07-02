package com.ironz.binaryprefs.file.adapter;

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
     * Removes file by key.
     *
     * @param name file name with extension
     */
    void remove(String name);
}