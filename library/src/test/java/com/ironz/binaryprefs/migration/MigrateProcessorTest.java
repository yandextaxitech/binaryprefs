package com.ironz.binaryprefs.migration;

import com.ironz.binaryprefs.Preferences;
import com.ironz.binaryprefs.PreferencesCreator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MigrateProcessorTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private MigrateProcessor processor;
    private PreferencesCreator creator;

    @Before
    public void setUp() throws Exception {
        creator = new PreferencesCreator();
        processor = new MigrateProcessor();
    }

    @Test
    public void emptyOne() {
        Preferences userPreferences = creator.create("user_preferences", folder);
        processor.add(userPreferences);
    }
}