package com.ironz.binaryprefs.dump;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class DumpReceiver extends BroadcastReceiver {

    private static final String PREF_NAME = "pref_name";

    @Override
    public void onReceive(Context context, Intent intent) {

        String packageName = context.getPackageName();
        String directoryPath = intent.getStringExtra(PREF_NAME);
        String prefName = intent.getStringExtra(PREF_NAME);


    }
}