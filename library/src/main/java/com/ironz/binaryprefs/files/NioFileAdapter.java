package com.ironz.binaryprefs.files;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Concrete file adapter which implements NIO file operations
 */
// TODO: 12/12/16 create benchmarks for MappedByteBuffer and plain direct ByteBuffer/ByteChannel
// TODO: 13/12/16 implement tests
public class NioFileAdapter implements FileAdapter {

    private final File srcDir;

    public NioFileAdapter(File srcDir) {
        this.srcDir = srcDir;
    }

    File getSrcDir() {
        return srcDir;
    }

    @Override
    public String[] names() {
        return srcDir.list();
    }

    @Override
    public byte[] fetch(String name) {
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
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: 16/12/16 implement logger
        } finally {
            try {
                if (randomAccessFile != null) randomAccessFile.close();
                if (channel != null) channel.close();
            } catch (IOException ignored) {
            }
        }

        return new byte[0];
    }

    @Override
    public void save(String name, byte[] bytes) {
        FileChannel channel = null;
        RandomAccessFile randomAccessFile = null;
        try {
            File file = new File(srcDir, name);
            randomAccessFile = new RandomAccessFile(file, "rw");
            channel = randomAccessFile.getChannel();
            MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            byteBuffer.put(bytes);
            channel.write(byteBuffer);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: 16/12/16 implement logger
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

        try {
            for (File file : srcDir.listFiles()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    allDeleted = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            allDeleted = false;
            // TODO: 16/12/16 implement logger
        }

        return allDeleted;
    }

    @Override
    public boolean remove(String name) {
        try {
            File file = new File(srcDir, name);
            return file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: 16/12/16 implement logger
        }

        return false;
    }
}
