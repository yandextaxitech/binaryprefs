package com.ironz.binaryprefs.file.check;

import android.os.Environment;

public final class AndroidExternalStorageChecker implements StorageChecker {

    @Override
    public boolean isStorageAvailable() {
        String storageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(storageState);
    }

    @Override
    public boolean isStorageWritable() {
        String storageState = Environment.getExternalStorageState();
        return !Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState);
    }
}
