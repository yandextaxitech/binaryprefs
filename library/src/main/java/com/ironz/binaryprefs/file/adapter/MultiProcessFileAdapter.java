package com.ironz.binaryprefs.file.adapter;

import android.content.Context;
import android.content.Intent;
import com.ironz.binaryprefs.file.FileAdapter;

public final class MultiProcessFileAdapter implements FileAdapter {

    private final String baseDir;

    public MultiProcessFileAdapter(Context context, String baseDir) {
        this.baseDir = baseDir;
        init(context);
    }

    private void init(Context context) {
        BlockingServiceBinder binder = new BlockingServiceBinder(context);
        Intent intent = new Intent(context, FileTransactionService.class);
        intent.putExtra(FileTransactionService.BASE_DIRECTORY, baseDir);
        binder.bindService(intent);
    }


    @Override
    public String[] names(String baseDir) {
        return new String[0];
    }

    @Override
    public byte[] fetch(String name) {
        return new byte[0];
    }

    @Override
    public void save(String name, byte[] bytes) {

    }

    @Override
    public void remove(String name) {

    }
}