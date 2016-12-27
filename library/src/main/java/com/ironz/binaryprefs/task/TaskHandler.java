package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.cache.CacheAdapter;
import com.ironz.binaryprefs.files.FileAdapter;

import java.util.Map;
import java.util.Set;

public class TaskHandler {

    private final CacheAdapter cacheAdapter;
    private final FileAdapter fileAdapter;

    public TaskHandler(CacheAdapter cacheAdapter, FileAdapter fileAdapter) {
        this.cacheAdapter = cacheAdapter;
        this.fileAdapter = fileAdapter;
    }

    public void apply(boolean clear, Set<String> removeSet, Map<String, Object> commitMap) {

    }

    public boolean commit(boolean clear, Set<String> removeSet, Map<String, Object> commitMap) {
        return false;
    }
}
