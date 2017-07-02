package com.ironz.binaryprefs.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.ironz.binaryprefs.BinaryPreferences;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.hello);

        BinaryPreferences preferences = App.getPreferences();

        updateTextView(preferences);

        listenChanges(preferences);

        startServiceInAnotherProcess();
    }

    private void listenChanges(BinaryPreferences preferences) {
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                updateTextView(sharedPreferences);
            }
        });
    }

    private void updateTextView(SharedPreferences sharedPreferences) {
        String time = "" + sharedPreferences.getLong("nano_time", 0L);
        textView.setText(time);
    }

    private void startServiceInAnotherProcess() {
        Intent intent = new Intent(this, RequestService.class);
        startService(intent);
    }
}