package com.ironz.binaryprefs.sample;

import android.app.Application;
import com.ironz.binaryprefs.BinaryPreferences;

public class App extends Application {

    private static BinaryPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        final PreferencesConfig preferencesConfig = new PreferencesConfig();
        preferences = preferencesConfig.createBinaryPreferences(this);
    }

    public static BinaryPreferences getPreferences() {
        return preferences;
    }
}