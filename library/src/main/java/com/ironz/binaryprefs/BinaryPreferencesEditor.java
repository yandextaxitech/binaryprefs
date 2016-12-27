package com.ironz.binaryprefs;

import android.content.SharedPreferences;
import com.ironz.binaryprefs.task.TaskHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class BinaryPreferencesEditor implements SharedPreferences.Editor {

    private final Map<String, Object> commitMap = new HashMap<>();
    private final Set<String> removeSet = new HashSet<>();

    private final TaskHandler taskHandler;

    private boolean clear;

    BinaryPreferencesEditor(TaskHandler taskHandler) {
        this.taskHandler = taskHandler;
    }

    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        commitMap.put(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        commitMap.put(key, values);
        return this;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        commitMap.put(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        commitMap.put(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        commitMap.put(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        commitMap.put(key, value);
        return this;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        removeSet.add(key);
        return this;
    }

    @Override
    public SharedPreferences.Editor clear() {
        clear = true;
        return this;
    }

    @Override
    public void apply() {
        taskHandler.apply(clear, removeSet, commitMap);
    }

    @Override
    public boolean commit() {
        return taskHandler.commit(clear, removeSet, commitMap);
    }
}
