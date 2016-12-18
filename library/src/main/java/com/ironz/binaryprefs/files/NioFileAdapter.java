package com.ironz.binaryprefs.files;

import com.ironz.binaryprefs.exception.FileOperationException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Concrete file adapter which implements NIO file operations
 */
// TODO: 12/12/16 create benchmarks for MappedByteBuffer and plain direct ByteBuffer/ByteChannel
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
            throw new FileOperationException(e);
        } finally {
            try {
                if (randomAccessFile != null) randomAccessFile.close();
                if (channel != null) channel.close();
            } catch (Exception ignored) {
            }
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ForLoopReplaceableByForEach"})
    @Override
    public void clear() {
        try {
            File[] files = srcDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                file.delete();
            }
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void remove(String name) {
        try {
            File file = new File(srcDir, name);
            file.delete();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }
}
