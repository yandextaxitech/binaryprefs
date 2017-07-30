package com.ironz.binaryprefs.file.adapter;

import com.ironz.binaryprefs.exception.FileOperationException;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * File adapter implementation which performs NIO file operations.
 * This implementation support backups for each file before new data
 * will be written. See {@link #backupAndSave(String, byte[])}.
 * After success write backup file will be removed.
 * If adapter detects backup file it will be replaced
 * to original file. See {@link #fetchBackupOrOriginal(String)}.
 */
public final class NioFileAdapter implements FileAdapter {

    private static final String ZERO_BYTES_MESSAGE = "%s key's value is zero bytes for saving";

    private static final String[] EMPTY_STRING_NAMES_ARRAY = {};

    private static final String BACKUP_EXTENSION = ".bak";
    private static final String R_MODE = "r";
    private static final String RW_MODE = "rw";

    private final File baseDir;
    private final File backupDir;

    public NioFileAdapter(DirectoryProvider directoryProvider) {
        this.baseDir = directoryProvider.getStoreDirectory();
        this.backupDir = directoryProvider.getBackupDirectory();
    }

    @Override
    public String[] names() {
        return namesInternal();
    }

    private String[] namesInternal() {
        String[] list = baseDir.list();
        if (list == null) {
            return EMPTY_STRING_NAMES_ARRAY;
        }
        return list;
    }

    @Override
    public byte[] fetch(String name) {
        return fetchBackupOrOriginal(name);
    }

    private byte[] fetchBackupOrOriginal(String name) {
        File backupFile = new File(backupDir, name + BACKUP_EXTENSION);
        File file = new File(baseDir, name);
        if (backupFile.exists()) {
            delete(file);
            swap(backupFile, file);
        }
        return fetchInternal(file);
    }

    private byte[] fetchInternal(File file) {
        FileChannel channel = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, R_MODE);
            channel = randomAccessFile.getChannel();
            int size = (int) randomAccessFile.length();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
            byte[] bytes = new byte[size];
            buffer.get(bytes);
            return bytes;
        } catch (Exception e) {
            throw new FileOperationException(e);
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void save(String name, byte[] bytes) {
        backupAndSave(name, bytes);
    }

    private void backupAndSave(String name, byte[] bytes) {
        if (bytes.length == 0) {
            throw new FileOperationException(String.format(ZERO_BYTES_MESSAGE, name));
        }
        File file = new File(baseDir, name);
        File backupFile = new File(backupDir, name + BACKUP_EXTENSION);
        swap(file, backupFile);
        saveInternal(file, bytes);
        delete(backupFile);
    }

    private void saveInternal(File file, byte[] bytes) {
        FileChannel channel = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, RW_MODE);
            randomAccessFile.setLength(0);
            channel = randomAccessFile.getChannel();
            MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            byteBuffer.put(bytes);
            channel.write(byteBuffer);
            byteBuffer.force();
        } catch (Exception e) {
            throw new FileOperationException(e);
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void swap(File from, File to) {
        if (!from.exists()) {
            return;
        }
        if (to.exists()) {
            //noinspection ResultOfMethodCallIgnored
            to.delete();
        }
        //noinspection ResultOfMethodCallIgnored
        from.renameTo(to);
    }

    private void delete(File file) {
        if (!file.exists()) {
            return;
        }
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    @Override
    public void remove(String name) {
        removeInternal(name);
    }

    private void removeInternal(String name) {
        try {
            File file = new File(baseDir, name);
            if (!file.exists()) {
                return;
            }
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }
}