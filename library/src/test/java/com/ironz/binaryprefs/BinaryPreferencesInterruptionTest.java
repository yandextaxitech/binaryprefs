package com.ironz.binaryprefs;

import com.ironz.binaryprefs.event.ExceptionHandler;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.task.TaskExecutor;
import com.ironz.binaryprefs.task.TestNewSingleThreadTaskExecutor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BinaryPreferencesInterruptionTest {

    private static final String PREFERENCES_NAME = "user_preferences";
    private static final String SECOND_PREFERENCES_NAME = "second_user_preferences";

    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String DEFAULT_VALUE = "defaultValue";

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private Preferences preferences;
    private File srcDir;
    private File backupDir;
    private File lockDir;

    @Before
    public void setUp() throws Exception {
        srcDir = folder.newFolder("preferences");
        backupDir = folder.newFolder("backup");
        lockDir = folder.newFolder("lock");

        preferences = create(
                PREFERENCES_NAME,
                srcDir,
                backupDir,
                lockDir
        );
    }

    @Test
    public void commitSucceedsWhenThreadInterrupted() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().interrupt();

                preferences.edit()
                        .putString(KEY, VALUE)
                        .commit();

                assertEquals(VALUE, preferences.getString(KEY, DEFAULT_VALUE));
                assertTrue(Thread.currentThread().isInterrupted());

                latch.countDown();
            }
        });
        thread.start();

        awaitWithTimeout(latch);

        assertEquals(VALUE, preferences.getString(KEY, DEFAULT_VALUE));
    }

    @Test
    public void applySucceedsWhenThreadInterrupted() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().interrupt();

                preferences.edit()
                        .putString(KEY, VALUE)
                        .apply();

                assertEquals(VALUE, preferences.getString(KEY, DEFAULT_VALUE));
                assertTrue(Thread.currentThread().isInterrupted());

                latch.countDown();
            }
        });
        thread.start();

        awaitWithTimeout(latch);
        assertEquals(VALUE, preferences.getString(KEY, DEFAULT_VALUE));
    }

    /**
     * Getting the value from the same preferences which were user to set value.
     * Preferences cache contains key value when getting value, thus "hot get".
     */
    @Test
    public void hotGetSucceedsWhenThreadInterrupted() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        preferences.edit()
                .putString(KEY, VALUE)
                .apply();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().interrupt();

                assertEquals(VALUE, preferences.getString(KEY, DEFAULT_VALUE));
                assertTrue(Thread.currentThread().isInterrupted());

                latch.countDown();
            }
        });
        thread.start();

        awaitWithTimeout(latch);
    }

    /**
     * Getting the value from the another preferences, which have the same dirs, but
     * do not share cache with preferences user to set value.
     * Preferences cache doesn't contain key value when getting value, thus "cold get".
     */
    @Test
    public void coldGetSucceedsWhenThreadInterrupted() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        preferences.edit()
                .putString(KEY, VALUE)
                .commit();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().interrupt();

                // Preferences share same dirs, but don't share value cache,
                // so getting a value required loading preferences from file.
                final Preferences secondPreferences = create(SECOND_PREFERENCES_NAME,
                        srcDir,
                        backupDir,
                        lockDir
                );

                assertEquals(VALUE, secondPreferences.getString(KEY, DEFAULT_VALUE));
                assertTrue(Thread.currentThread().isInterrupted());

                latch.countDown();
            }
        });
        thread.start();

        awaitWithTimeout(latch);
    }

    private static void awaitWithTimeout(CountDownLatch latch) throws InterruptedException {
        final boolean inTime = latch.await(20, TimeUnit.SECONDS);
        assertTrue(inTime);
    }

    private static Preferences create(String name,
                                      final File srcDir,
                                      final File backupDir,
                                      final File lockDir) {

        final DirectoryProvider directoryProvider = new DirectoryProvider() {
            @Override
            public File getStoreDirectory() {
                return srcDir;
            }

            @Override
            public File getBackupDirectory() {
                return backupDir;
            }

            @Override
            public File getLockDirectory() {
                return lockDir;
            }
        };

        final ExceptionHandler exceptionHandler = new ExceptionHandler() {
            @Override
            public void handle(Exception e) {
                // Fail on all exceptions.
                fail(e.toString());
            }
        };

        // The executor shouldn't run tasks on current thread,
        // so any tasks submitted to it won't fail once "current" thread is interrupted.
        // For example, NIO operations would fail on interruption.
        final TaskExecutor taskExecutor = new TestNewSingleThreadTaskExecutor(
                exceptionHandler);

        return new PreferencesCreator().create(
                name,
                directoryProvider,
                new ConcurrentHashMap<String, ReadWriteLock>(),
                new ConcurrentHashMap<String, Lock>(),
                new ConcurrentHashMap<String, Set<String>>(),
                new ConcurrentHashMap<String, Map<String, Object>>(),
                taskExecutor
        );
    }
}
