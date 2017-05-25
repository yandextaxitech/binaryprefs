package com.ironz.binaryprefs.file.directory;

import android.content.Context;

import java.io.File;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class AndroidDirectoryProviderImpl implements DirectoryProvider {

    private final File prefs;

    public AndroidDirectoryProviderImpl(Context context, String prefName) {
        this(context, prefName, false);
    }

    public AndroidDirectoryProviderImpl(Context context, String prefName, boolean saveInExternal) {
        File cacheDir = saveInExternal ? context.getExternalCacheDir() : context.getCacheDir();
        prefs = new File(cacheDir, prefName);
        //noinspection ResultOfMethodCallIgnored
        prefs.mkdirs();
    }

    @Override
    public File getBaseDirectory() {
        return prefs;
    }
}