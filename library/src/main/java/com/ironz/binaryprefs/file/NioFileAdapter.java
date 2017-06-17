package com.ironz.binaryprefs.file;

import com.ironz.binaryprefs.encryption.ByteEncryption;
import com.ironz.binaryprefs.exception.FileOperationException;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete file adapter which implements NIO file operations
 */
public final class NioFileAdapter implements FileAdapter {

    private static final String BACKUP_EXTENSION = ".bak";
    private static final String[] EMPTY_STRING_NAMES_ARRAY = new String[0];

    private static final String READ_MODE = "r";
    private static final String RWD_MODE = "rwd";

    private final File srcDir;
    private final ByteEncryption encryption;
    private final PersistenceHandler persistenceHandler;

    @SuppressWarnings("WeakerAccess")
    public NioFileAdapter(DirectoryProvider directoryProvider) {
        this(directoryProvider.getBaseDirectory(), ByteEncryption.NO_OP, PersistenceHandler.NO_OP);
    }

    @SuppressWarnings("WeakerAccess")
    public NioFileAdapter(DirectoryProvider directoryProvider, ByteEncryption encryption) {
        this(directoryProvider.getBaseDirectory(), encryption, PersistenceHandler.NO_OP);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public NioFileAdapter(DirectoryProvider directoryProvider, ByteEncryption encryption, PersistenceHandler persistenceHandler) {
        this(directoryProvider.getBaseDirectory(), encryption, persistenceHandler);
    }

    @SuppressWarnings("WeakerAccess")
    private NioFileAdapter(File srcDir, ByteEncryption encryption, PersistenceHandler persistenceHandler) {
        this.srcDir = srcDir;
        this.encryption = encryption;
        this.persistenceHandler = persistenceHandler;
    }

    @Override
    public String[] names() {
        return namesInternal();
    }

    private String[] namesInternal() {
        String[] list = srcDir.list();

        if (list == null) {
            return EMPTY_STRING_NAMES_ARRAY;
        }

        final List<String> names = new ArrayList<>();

        for (String name : list) {
            if (name.endsWith(BACKUP_EXTENSION)) {
                continue;
            }
            names.add(name);
        }

        return names.toArray(EMPTY_STRING_NAMES_ARRAY);
    }

    @Override
    public byte[] fetch(String name) {
        byte[] bytes = fetchBackupOrOriginal(name);
        return encryption.decrypt(bytes);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] fetchBackupOrOriginal(String name) {
        File backupFile = new File(srcDir, name + BACKUP_EXTENSION);
        File file = new File(srcDir, name);
        if (backupFile.exists()) {
            deleteOriginal(file);
            swap(backupFile, file);
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
            randomAccessFile = new RandomAccessFile(file, READ_MODE);
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
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void save(String name, byte[] bytes) {
        backupAndSave(name, bytes);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void backupAndSave(String name, byte[] bytes) {
        File file = new File(srcDir, name);
        File backupFile = new File(srcDir, file.getName() + BACKUP_EXTENSION);
        byte[] encrypt = encryption.encrypt(bytes);
        swap(file, backupFile);
        saveInternal(file, encrypt);
        deleteBackup(backupFile);
        persistenceHandler.onSuccess(name);
    }

    private void deleteBackup(File backupFile) {
        //noinspection ResultOfMethodCallIgnored
        backupFile.delete();
    }

    private void swap(File from, File to) {
        //noinspection ResultOfMethodCallIgnored
        from.renameTo(to);
    }

    private void saveInternal(File file, byte[] bytes) {
        FileChannel channel = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, RWD_MODE);
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

    @Override
    public void remove(String name) {
        removeInternal(name);
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

    File getSrcDir() {
        return srcDir;
    }
}