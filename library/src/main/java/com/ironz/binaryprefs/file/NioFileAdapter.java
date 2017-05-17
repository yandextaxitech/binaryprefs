package com.ironz.binaryprefs.file;

import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.exception.FileOperationException;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Concrete file adapter which implements NIO file operations
 */
public final class NioFileAdapter implements FileAdapter {

    private static final String BACKUP_EXTENSION = ".bak";

    final File srcDir;
    private final TaskExecutor taskExecutor;
    private final ByteEncryption encryption;

    @SuppressWarnings({"WeakerAccess", "unused"})
    public NioFileAdapter(DirectoryProvider directoryProvider) {
        this(directoryProvider, TaskExecutor.DEFAULT);
    }

    @SuppressWarnings("WeakerAccess")
    public NioFileAdapter(DirectoryProvider directoryProvider, TaskExecutor taskExecutor) {
        this(directoryProvider, taskExecutor, ByteEncryption.NO_OP);
    }

    @SuppressWarnings({"WeakerAccess"})
    public NioFileAdapter(DirectoryProvider directoryProvider, TaskExecutor taskExecutor, ByteEncryption byteEncryption) {
        this(directoryProvider.getBaseDirectory(), taskExecutor, byteEncryption);
    }

    @SuppressWarnings("WeakerAccess")
    private NioFileAdapter(File srcDir, TaskExecutor taskExecutor, ByteEncryption encryption) {
        this.srcDir = srcDir;
        this.taskExecutor = taskExecutor;
        this.encryption = encryption;
    }

    private String[] namesInternal() {
        String[] list = srcDir.list();
        if (list != null) {
            return list;
        }
        return new String[0];
    }

    @Override
    public String[] names() {
        return namesInternal();
    }

    @Override
    public byte[] fetch(String name) {
        return fetchBackupOrOriginal(name);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] fetchBackupOrOriginal(String name) {
        File backupFile = new File(srcDir, name + BACKUP_EXTENSION);
        File file = new File(srcDir, name);
        if (backupFile.exists()) {
            deleteOriginal(file);
            swapFiles(backupFile, file);
        }
        return fetchInternal(file);
    }

    private void deleteOriginal(File file) {
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    private byte[] fetchInternal(File file) {
        FileChannel channel = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
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
                if (randomAccessFile != null) randomAccessFile.close();
                if (channel != null) channel.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void save(final String name, final byte[] bytes) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                backupAndSave(name, bytes);
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void backupAndSave(String name, byte[] bytes) {
        File file = new File(srcDir, name);
        File backupFile = new File(srcDir, file.getName() + BACKUP_EXTENSION);
        byte[] encrypt = encryption.encrypt(bytes);
        swapFiles(file, backupFile);
        saveInternal(file, encrypt);
        deleteBackup(backupFile);
    }

    private void deleteBackup(File backupFile) {
        //noinspection ResultOfMethodCallIgnored
        backupFile.delete();
    }

    private void swapFiles(File from, File to) {
        //noinspection ResultOfMethodCallIgnored
        from.renameTo(to);
    }

    private void saveInternal(File file, byte[] bytes) {
        FileChannel channel = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rwd");
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
                if (randomAccessFile != null) randomAccessFile.close();
                if (channel != null) channel.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void clear() {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                clearInternal();
            }
        });
    }

    private void clearInternal() {
        try {
            for (String name : namesInternal()) {
                File file = new File(srcDir, name);
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public void remove(final String name) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                removeInternal(name);
            }
        });
    }

    private void removeInternal(String name) {
        try {
            File file = new File(srcDir, name);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public boolean contains(String name) {
        File file = new File(srcDir, name);
        return file.exists();
    }
}