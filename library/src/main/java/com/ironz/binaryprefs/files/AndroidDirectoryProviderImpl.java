package com.ironz.binaryprefs.files;

import android.content.Context;

import java.io.File;


@SuppressWarnings("unused")
public final class AndroidDirectoryProviderImpl implements DirectoryProvider {

    private final File prefs;

    public AndroidDirectoryProviderImpl(Context context, String prefName) {
        File cacheDir = context.getCacheDir();
        prefs = new File(cacheDir, prefName);
        //noinspection ResultOfMethodCallIgnored
        prefs.mkdirs();
    }

    @Override
    public File getBaseDirectory() {
        return prefs;
    }
}