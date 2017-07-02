package com.ironz.binaryprefs.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.ironz.binaryprefs.BinaryPreferences;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PreferencesConfig preferencesConfig = new PreferencesConfig();
        final BinaryPreferences preferences = preferencesConfig.createBinaryPreferences(this);

        final TextView textView = (TextView) findViewById(R.id.hello);

        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                String time = "" + sharedPreferences.getLong("nano_time", 0L);
                textView.setText(time);
            }
        });

        Intent intent = new Intent(this, RequestService.class);
        startService(intent);
    }
}