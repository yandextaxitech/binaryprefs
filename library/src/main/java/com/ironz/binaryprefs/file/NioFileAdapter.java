package com.ironz.binaryprefs.file;

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

    private final Map<String, byte[]> cache = new HashMap<>();

    public NioFileAdapter(File srcDir, TaskExecutor taskExecutor) {
        this.srcDir = srcDir;
        this.taskExecutor = taskExecutor;
        defineCache();
    }

    @SuppressWarnings("WeakerAccess")
    public NioFileAdapter(DirectoryProvider directoryProvider, TaskExecutor taskExecutor) {
        this.srcDir = directoryProvider.getBaseDirectory();
        this.taskExecutor = taskExecutor;
        defineCache();
    }

    private void defineCache() {
        for (String name : getFileNamesInternal()) {
            File file = new File(srcDir, name);
            byte[] bytes = fetchInternal(file);
            cache.put(name, bytes);
        }
    }

    @Override
    public String[] names() {
        return cache.keySet().toArray(new String[0]);
    }

    @Override
    public byte[] fetch(String name) {
        return cache.get(name);
    }

    @Override
    public void save(final String name, final byte[] bytes) {
        cache.put(name, bytes);
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                File file = new File(srcDir, name);
                saveInternal(file, bytes);
            }
        });
    }

    private String[] getFileNamesInternal() {
        String[] list = srcDir.list();
        if (list != null) {
            return list;
        }
        return new String[0];
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
}