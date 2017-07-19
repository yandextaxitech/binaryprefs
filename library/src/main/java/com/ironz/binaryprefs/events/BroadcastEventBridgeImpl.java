package com.ironz.binaryprefs.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Process;
import com.ironz.binaryprefs.Preferences;
import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.encryption.ValueEncryption;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Uses global broadcast receiver mechanism for delivering all key change events.
 * Main propose for using this implementation is IPC mechanism.
 * This bridge optimized even if broadcast comes in local process.
 * Uses UI thread for delivering key changes.
 */
@SuppressWarnings("unused")
public final class BroadcastEventBridgeImpl implements EventBridge {

    private static final String INTENT_PREFIX = "com.ironz.binaryprefs.";
    private static final String ACTION_PREFERENCE_UPDATED = INTENT_PREFIX + "ACTION_PREFERENCE_UPDATED_";
    private static final String ACTION_PREFERENCE_REMOVED = INTENT_PREFIX + "ACTION_PREFERENCE_REMOVED_";

    private static final String PREFERENCE_NAME = "preference_name";
    private static final String PREFERENCE_KEY = "preference_key";
    private static final String PREFERENCE_VALUE = "preference_value";
    private static final String PREFERENCE_PROCESS_ID = "preference_process_id";

    private static final Map<String, List<OnSharedPreferenceChangeListenerWrapper>> allListeners = new ConcurrentHashMap<>();
    private final List<OnSharedPreferenceChangeListenerWrapper> listeners;

    private final Handler handler = new Handler();

    private final Context context;
    private final String prefName;
    private final CacheProvider cacheProvider;
    private final SerializerFactory serializerFactory;
    private final TaskExecutor taskExecutor;
    private final ValueEncryption valueEncryption;

    private final String updateActionName;
    private final String removeActionName;
    private final int processId;
    private final BroadcastReceiver updateReceiver;
    private final BroadcastReceiver removeReceiver;

    public BroadcastEventBridgeImpl(Context context,
                                    String prefName,
                                    CacheProvider cacheProvider,
                                    SerializerFactory serializerFactory,
                                    TaskExecutor taskExecutor,
                                    ValueEncryption valueEncryption) {
        this.context = context;
        this.prefName = prefName;
        this.cacheProvider = cacheProvider;
        this.serializerFactory = serializerFactory;
        this.taskExecutor = taskExecutor;
        this.valueEncryption = valueEncryption;
        this.updateActionName = createUpdateActionName(context);
        this.removeActionName = createRemoveActionName(context);
        this.listeners = initListeners(prefName);
        this.updateReceiver = createUpdateReceiver();
        this.removeReceiver = createRemoveReceiver();
        this.processId = Process.myPid();
    }

    private String createUpdateActionName(Context context) {
        return ACTION_PREFERENCE_UPDATED + context.getPackageName();
    }

    private String createRemoveActionName(Context context) {
        return ACTION_PREFERENCE_REMOVED + context.getPackageName();
    }

    private List<OnSharedPreferenceChangeListenerWrapper> initListeners(String prefName) {
        if (allListeners.containsKey(prefName)) {
            return allListeners.get(prefName);
        }
        List<OnSharedPreferenceChangeListenerWrapper> listeners = new ArrayList<>();
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
        this.context.registerReceiver(updateReceiver, new IntentFilter(updateActionName));
        this.context.registerReceiver(removeReceiver, new IntentFilter(removeActionName));
    }

    private void notifyUpdate(final Intent intent) {
        if (!prefName.equals(intent.getStringExtra(PREFERENCE_NAME))) {
            return;
        }
        if (processId == intent.getIntExtra(PREFERENCE_PROCESS_ID, 0)) {
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
        if (processId == intent.getIntExtra(PREFERENCE_PROCESS_ID, 0)) {
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
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListenerWrapper listener) {
        if (listeners.isEmpty()) {
            subscribeReceivers();
        }
        listeners.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListenerWrapper listener) {
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            unSubscribeReceivers();
        }
    }

    private void unSubscribeReceivers() {
        context.unregisterReceiver(updateReceiver);
        context.unregisterReceiver(removeReceiver);
    }

    @Override
    public void notifyListenersUpdate(Preferences preferences, String key, byte[] bytes) {
        notifyListenersHandler(key);
        sendUpdateIntent(key, bytes);
    }

    @Override
    public void notifyListenersRemove(Preferences preferences, String key) {
        notifyListenersHandler(key);
        sendRemoveIntent(key);
    }

    private void update(String key, Object value) {
        cacheProvider.put(key, value);
        notifyListenersHandler(key);
    }

    private void remove(String key) {
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
        for (OnSharedPreferenceChangeListenerWrapper listener : listeners) {
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