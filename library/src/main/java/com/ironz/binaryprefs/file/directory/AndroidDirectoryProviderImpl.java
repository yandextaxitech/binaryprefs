package com.ironz.binaryprefs.file.directory;

import android.content.Context;

import java.io.File;

/**
 * Provides default android cache directory or external (if possible) cache directory.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class AndroidDirectoryProviderImpl implements DirectoryProvider {

    private final File prefs;

    /**
     * Creates instance for default app cache directory.
     *
     * @param context  target app context
     * @param prefName preferences name
     */
    public AndroidDirectoryProviderImpl(Context context, String prefName) {
        this(context, prefName, false);
    }

    /**
     * Creates instance for default or external (if possible) app cache directory.
     *
     * @param context        target app context
     * @param prefName       preferences name
     * @param saveInExternal all data will be saved inside external cache directory
     *                       if <code>true</code> value is passed
     *                       ({@link Context#getExternalCacheDir()}),
     *                       if <code>false</code> - will use standard app cache directory
     *                       ({@link Context#getCacheDir()}).
     */
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