package com.ironz.binaryprefs.task;

import com.ironz.binaryprefs.files.FileAdapter;

import java.util.Map;
import java.util.Set;

public class TaskHandler {

    private final FileAdapter fileAdapter;

    public TaskHandler(FileAdapter fileAdapter) {
        this.fileAdapter = fileAdapter;
    }

    public void apply(boolean clear, Set<String> removeSet, Map<String, byte[]> commitMap) {

        if (clear) {
            fileAdapter.clear();
        }

        for (String s : removeSet) {
            fileAdapter.remove(s);
        }

        for (Map.Entry<String, byte[]> entry : commitMap.entrySet()) {
            fileAdapter.save(entry.getKey(), entry.getValue());
        }
    }

    public boolean commit(boolean clear, Set<String> removeSet, Map<String, byte[]> commitMap) {

        try {

            if (clear) {
                fileAdapter.clear();
            }

            for (String s : removeSet) {
                fileAdapter.remove(s);
            }

            for (Map.Entry<String, byte[]> entry : commitMap.entrySet()) {
                fileAdapter.save(entry.getKey(), entry.getValue());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}