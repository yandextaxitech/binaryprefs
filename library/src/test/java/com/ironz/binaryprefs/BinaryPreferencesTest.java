package com.ironz.binaryprefs;

import com.ironz.binaryprefs.files.NioFileAdapter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

        BinaryPreferences preferences = new BinaryPreferences(new NioFileAdapter(newFolder));

        String bool = "bool";
        String str = "str";
        String ss = "ss";

        HashSet<String> strings = new HashSet<>();
        strings.add("one");
        strings.add("two");
        strings.add("tree");

        preferences.edit()
                .putBoolean(bool, true)
                .putString(str, "value")
                .putStringSet(ss, strings)
                .apply();

        boolean boo = preferences.getBoolean(bool, false);
        String string = preferences.getString(str, "val2");
        Set<String> stringSet = preferences.getStringSet(ss, new HashSet<>());

        System.out.println("bool: " + boo);
        System.out.println("string: " + string);
        System.out.println("strings set: " + Arrays.toString(stringSet.toArray()));
    }
}