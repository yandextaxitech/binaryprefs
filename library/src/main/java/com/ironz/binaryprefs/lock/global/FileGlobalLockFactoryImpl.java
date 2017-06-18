package com.ironz.binaryprefs.lock.global;

import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.exception.FileOperationException;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implement file lock which locks on file globally (between processes)
 */
public final class FileGlobalLockFactoryImpl implements GlobalLockFactory {

    private static final String LOCK_EXTENSION = ".lock";
    private static final String RW_MODE = "rw";
    private static final String KEY = FileGlobalLockFactoryImpl.class.getName();

    private final File file;
    private final ExceptionHandler exceptionHandler;
    private final FileChannel channel;

    @SuppressWarnings("unused")
    public FileGlobalLockFactoryImpl(DirectoryProvider directoryProvider) {
        this(directoryProvider, ExceptionHandler.IGNORE);
    }

    public FileGlobalLockFactoryImpl(DirectoryProvider directoryProvider, ExceptionHandler exceptionHandler) {
        this.file = new File(directoryProvider.getBaseDirectory(), LOCK_EXTENSION);
        this.exceptionHandler = exceptionHandler;
        this.channel = getFileChannel();
    }

    private FileChannel getFileChannel() {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, RW_MODE);
            if (!file.exists()) {
                randomAccessFile.write(0);
            }
            return randomAccessFile.getChannel();
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    @Override
    public Lock getLock() {
        try {
            return new GlobalLockImpl(channel);
        } catch (Exception e) {
            exceptionHandler.handle(KEY, e);
        }
        return new ReentrantLock(true);
    }
}