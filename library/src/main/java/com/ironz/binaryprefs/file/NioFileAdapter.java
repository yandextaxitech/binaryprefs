package com.ironz.binaryprefs.file;

import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.exception.FileOperationException;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete file adapter which implements NIO file operations
 */
public final class NioFileAdapter implements FileAdapter {

    final File srcDir;
    private final TaskExecutor taskExecutor;
    private final ByteEncryption encryption;

    private final Map<String, byte[]> cache = new HashMap<>();

    @SuppressWarnings({"WeakerAccess", "unused"})
    public NioFileAdapter(DirectoryProvider directoryProvider, TaskExecutor taskExecutor, ByteEncryption byteEncryption) {
        this(directoryProvider.getBaseDirectory(), taskExecutor, byteEncryption);
    }

    @SuppressWarnings("WeakerAccess")
    public NioFileAdapter(DirectoryProvider directoryProvider, TaskExecutor taskExecutor) {
        this(directoryProvider.getBaseDirectory(), taskExecutor, ByteEncryption.NO_OP);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public NioFileAdapter(DirectoryProvider directoryProvider) {
        this(directoryProvider.getBaseDirectory(), TaskExecutor.DEFAULT, ByteEncryption.NO_OP);
    }

    @SuppressWarnings("WeakerAccess")
    public NioFileAdapter(File srcDir, TaskExecutor taskExecutor, ByteEncryption encryption) {
        this.srcDir = srcDir;
        this.taskExecutor = taskExecutor;
        this.encryption = encryption;
    }

    @Override
    public String[] names() {
        return cache.keySet().toArray(new String[0]);
    }

    @Override
    public byte[] fetch(String name) {
        if (cache.containsKey(name)) {
            System.out.println("contains key");
            return cache.get(name);
        }
        System.out.println("not contains key");
        File file = new File(srcDir, name);
        byte[] bytes = fetchInternal(file);
        byte[] decrypt = encryption.decrypt(bytes);
        cache.put(name, decrypt);
        return decrypt;
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
        cache.put(name, bytes);
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                File file = new File(srcDir, name);
                byte[] encrypt = encryption.encrypt(bytes);
                saveInternal(file, encrypt);
            }
        });
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
        cache.clear();
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                clearInternal();
            }
        });
    }

    private void clearInternal() {
        try {
            String[] list = names();
            String[] files = list != null ? list : new String[0];
            for (String name : files) {
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
        cache.remove(name);
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
        return cache.containsKey(name);
    }

    @Override
    public void evictCache() {
        cache.clear();
    }
}