package com.ironz.binaryprefs.files;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Concrete file adapter which implements NIO file operations
 */
// TODO: 12/12/16 create benchmarks for MappedByteBuffer and plain direct ByteBuffer
// TODO: 13/12/16 implement tests
public class NioFileAdapter implements FileAdapter {

    private final File srcDir;

    public NioFileAdapter(File srcDir) {
        this.srcDir = srcDir;
    }

    @Override
    public String[] names() {
        return srcDir.list();
    }

    @Override
    public byte[] fetch(String name) throws Exception {
        FileChannel channel = null;
        RandomAccessFile randomAccessFile = null;
        try {
            File file = new File(srcDir, name);
            randomAccessFile = new RandomAccessFile(file, "r");
            channel = randomAccessFile.getChannel();
            int size = (int) randomAccessFile.length();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
            byte[] bytes = new byte[size];
            buffer.get(bytes);
            return bytes;
        } finally {
            try {
                if (randomAccessFile != null) randomAccessFile.close();
                if (channel != null) channel.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void save(String name, byte[] bytes) throws Exception {
        FileChannel channel = null;
        RandomAccessFile randomAccessFile = null;
        try {
            File file = new File(srcDir, name);
            randomAccessFile = new RandomAccessFile(file, "rw");
            channel = randomAccessFile.getChannel();
            MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            byteBuffer.put(bytes);
            channel.write(byteBuffer);
        } finally {
            try {
                if (randomAccessFile != null) randomAccessFile.close();
                if (channel != null) channel.close();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean clear() {
        boolean allDeleted = true;
        for (File file : srcDir.listFiles()) {
            boolean deleted = file.delete();
            if (!deleted) {
                allDeleted = false;
            }
        }
        return allDeleted;
    }

    @Override
    public boolean remove(String name) {
        File file = new File(srcDir, name);
        return file.delete();
    }
}
