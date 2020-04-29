package com.ironz.binaryprefs.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.ironz.binaryprefs.cache.candidates.CacheCandidateProvider;
import com.ironz.binaryprefs.cache.provider.CacheProvider;
import com.ironz.binaryprefs.encryption.ValueEncryption;
import com.ironz.binaryprefs.file.directory.DirectoryProvider;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Uses global broadcast receiver mechanism for delivering all key change events.
 * Main propose for using this implementation is IPC mechanism.
 * This bridge optimized even if broadcast comes in local process.
 * Uses UI thread for delivering key changes.
 */
public final class BroadcastEventBridge implements EventBridge {

    private static final String INTENT_PREFIX = "com.ironz.binaryprefs.";
    private static final String ACTION_PREFERENCE_UPDATED = INTENT_PREFIX + "ACTION_PREFERENCE_UPDATED_";
    private static final String ACTION_PREFERENCE_REMOVED = INTENT_PREFIX + "ACTION_PREFERENCE_REMOVED_";

    private static final String PREFERENCE_NAME = "preference_name";
    private static final String PREFERENCE_KEY = "preference_key";
    private static final String PREFERENCE_VALUE = "preference_value";
    private static final String PREFERENCE_PROCESS_ID = "preference_process_id";

    private static final int DEFAULT_PROCESS_ID = 0;

    private final List<OnSharedPreferenceChangeListener> currentListeners;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Context context;
    private final String prefName;
    private final CacheCandidateProvider cacheCandidateProvider;
    private final CacheProvider cacheProvider;
    private final SerializerFactory serializerFactory;
    private final TaskExecutor taskExecutor;
    private final ValueEncryption valueEncryption;

    private final String updateActionName;
    private final String removeActionName;
    private final int processId;
    private final BroadcastReceiver updateReceiver;
    private final BroadcastReceiver removeReceiver;

    public BroadcastEventBridge(Context context,
                                String prefName,
                                CacheCandidateProvider cacheCandidateProvider,
                                CacheProvider cacheProvider,
                                SerializerFactory serializerFactory,
                                TaskExecutor taskExecutor,
                                ValueEncryption valueEncryption,
                                DirectoryProvider directoryProvider,
                                Map<String, List<OnSharedPreferenceChangeListener>> allListeners) {
        this.context = context;
        this.prefName = prefName;
        this.cacheCandidateProvider = cacheCandidateProvider;
        this.cacheProvider = cacheProvider;
        this.serializerFactory = serializerFactory;
        this.taskExecutor = taskExecutor;
        this.valueEncryption = valueEncryption;
        this.updateActionName = createUpdateActionName(directoryProvider);
        this.removeActionName = createRemoveActionName(directoryProvider);
        this.currentListeners = putIfAbsentListeners(prefName, allListeners);
        this.updateReceiver = createUpdateReceiver();
        this.removeReceiver = createRemoveReceiver();
        this.processId = Process.myPid();
    }

    private String createUpdateActionName(DirectoryProvider directoryProvider) {
        return ACTION_PREFERENCE_UPDATED + directoryProvider.getStoreDirectory().getAbsolutePath();
    }

    private String createRemoveActionName(DirectoryProvider directoryProvider) {
        return ACTION_PREFERENCE_REMOVED + directoryProvider.getStoreDirectory().getAbsolutePath();
    }

    private List<OnSharedPreferenceChangeListener> putIfAbsentListeners(String prefName, Map<String,
            List<OnSharedPreferenceChangeListener>> allListeners) {
        if (allListeners.containsKey(prefName)) {
            return allListeners.get(prefName);
        }
        List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>();
        allListeners.put(prefName, listeners);
        return listeners;
    }

    private BroadcastReceiver createRemoveReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notifyRemove(intent);
            }
        };
    }

    private BroadcastReceiver createUpdateReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notifyUpdate(intent);
            }
        };
    }

    private void subscribeReceivers() {
        context.registerReceiver(updateReceiver, new IntentFilter(updateActionName));
        context.registerReceiver(removeReceiver, new IntentFilter(removeActionName));
    }

    private void notifyUpdate(final Intent intent) {
        if (!prefName.equals(intent.getStringExtra(PREFERENCE_NAME))) {
            return;
        }
        if (processId == intent.getIntExtra(PREFERENCE_PROCESS_ID, DEFAULT_PROCESS_ID)) {
            return;
        }

        final String key = intent.getStringExtra(PREFERENCE_KEY);
        final byte[] value = intent.getByteArrayExtra(PREFERENCE_VALUE);

        notifyUpdateTask(key, value);
    }

    private void notifyUpdateTask(final String key, final byte[] value) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                notifyUpdateInternal(key, value);
            }
        });
    }

    private void notifyUpdateInternal(String key, byte[] value) {
        Object o = fetchObject(key, value);
        update(key, o);
    }

    private Object fetchObject(String key, byte[] bytes) {
        byte[] decrypt = valueEncryption.decrypt(bytes);
        return serializerFactory.deserialize(key, decrypt);
    }

    private void notifyRemove(final Intent intent) {
        if (!prefName.equals(intent.getStringExtra(PREFERENCE_NAME))) {
            return;
        }
        if (processId == intent.getIntExtra(PREFERENCE_PROCESS_ID, DEFAULT_PROCESS_ID)) {
            return;
        }

        notifyRemoveTask(intent);
    }

    private void notifyRemoveTask(final Intent intent) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                notifyRemoveInternal(intent);
            }
        });
    }

    private void notifyRemoveInternal(Intent intent) {
        final String key = intent.getStringExtra(PREFERENCE_KEY);
        remove(key);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        if (currentListeners.isEmpty()) {
            subscribeReceivers();
        }
        currentListeners.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        currentListeners.remove(listener);
        if (currentListeners.isEmpty()) {
            unSubscribeReceivers();
        }
    }

    private void unSubscribeReceivers() {
        context.unregisterReceiver(updateReceiver);
        context.unregisterReceiver(removeReceiver);
    }

    @Override
    public void notifyListenersUpdate(String key, byte[] bytes) {
        notifyListenersHandler(key);
        sendUpdateIntent(key, bytes);
    }

    @Override
    public void notifyListenersRemove(String key) {
        notifyListenersHandler(key);
        sendRemoveIntent(key);
    }

    private void update(String key, Object value) {
        cacheCandidateProvider.put(key);
        cacheProvider.put(key, value);
        notifyListenersHandler(key);
    }

    private void remove(String key) {
        cacheCandidateProvider.remove(key);
        cacheProvider.remove(key);
        notifyListenersHandler(key);
    }

    private void notifyListenersHandler(final String key) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyListenersInternal(key);
            }
        });
    }

    private void notifyListenersInternal(String key) {
        for (OnSharedPreferenceChangeListener listener : currentListeners) {
            listener.onSharedPreferenceChanged(null, key);
        }
    }

    private void sendUpdateIntent(final String key, final byte[] bytes) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                sendUpdateIntentInternal(key, bytes);
            }
        });
    }

    private void sendRemoveIntent(final String key) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                sendRemoveIntentInternal(key);
            }
        });
    }

    private void sendUpdateIntentInternal(String key, byte[] bytes) {
        Intent intent = new Intent(updateActionName);
        intent.putExtra(PREFERENCE_PROCESS_ID, processId);
        intent.putExtra(PREFERENCE_NAME, prefName);
        intent.putExtra(PREFERENCE_KEY, key);
        intent.putExtra(PREFERENCE_VALUE, bytes);
        context.sendBroadcast(intent);
    }

    private void sendRemoveIntentInternal(String key) {
        Intent intent = new Intent(removeActionName);
        intent.putExtra(PREFERENCE_PROCESS_ID, processId);
        intent.putExtra(PREFERENCE_NAME, prefName);
        intent.putExtra(PREFERENCE_KEY, key);
        context.sendBroadcast(intent);
    }
}