package com.ironz.binaryprefs;

import com.ironz.binaryprefs.exception.ExceptionHandler;
import com.ironz.binaryprefs.exception.ExceptionHandlerImpl;
import com.ironz.binaryprefs.files.FileAdapter;
import com.ironz.binaryprefs.files.NioFileAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

public final class BinaryPreferencesTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();
    private File newFolder;

    @Before
    public void setUp() throws Exception {
        newFolder = folder.newFolder();
    }

    @Test
    public void base() {

        ExceptionHandler exceptionHandler = new ExceptionHandlerImpl();
        FileAdapter adapter = new NioFileAdapter(newFolder);
        BinaryPreferences preferences = new BinaryPreferences(adapter, exceptionHandler);

        String bool = "bool";
        String str = "str";
        String ss = "ss";

        HashSet<String> strings = new HashSet<>();
        strings.add("1one");
        strings.add("2two");
        strings.add("3tree");

        preferences.edit()
                .putBoolean(bool, true)
                .putString(str, "value")
                .putStringSet(ss, strings)
                .apply();

        System.out.println("\nfirst-----");
        System.out.println("bool: " + preferences.getBoolean(bool, false));
        System.out.println("string: " + preferences.getString(str, "default"));
        System.out.println("strings set: " + Arrays.toString(preferences.getStringSet(ss, new HashSet<String>()).toArray()));
        System.out.println("all: " + preferences.getAll().toString());

        preferences.edit().clear().apply();

        System.out.println("\nsecond-----");
        System.out.println("bool: " + preferences.getBoolean(bool, false));
        System.out.println("string: " + preferences.getString(str, "default"));
        System.out.println("strings set: " + Arrays.toString(preferences.getStringSet(ss, new HashSet<String>()).toArray()));
        System.out.println("all: " + preferences.getAll().toString());
    }
}