package com.ironz.binaryprefs;

import com.ironz.binaryprefs.files.NioFileAdapter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

public class BinaryPreferencesTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void name() throws IOException {
        File newFolder = folder.newFolder();
        BinaryPreferences preferences = new BinaryPreferences(new NioFileAdapter(newFolder));
        preferences.edit()
                .putBoolean("bool", true)
                .putString("str", "value")
                .apply();

        boolean bool = preferences.getBoolean("bool", false);
        String string = preferences.getString("str", "val2");

        System.out.println("bool: " + bool);
        System.out.println("string: " + string);
    }
}