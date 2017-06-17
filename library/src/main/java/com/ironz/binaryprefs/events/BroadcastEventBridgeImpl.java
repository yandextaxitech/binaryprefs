package com.ironz.binaryprefs.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;
import com.ironz.binaryprefs.Preferences;
import com.ironz.binaryprefs.cache.CacheProvider;
import com.ironz.binaryprefs.file.FileAdapter;
import com.ironz.binaryprefs.serialization.SerializerFactory;
import com.ironz.binaryprefs.task.TaskExecutor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Uses global broadcast receiver mechanism for delivering all key change events.
 * Main propose for using this implementation is IPC mechanism.
 */
@SuppressWarnings("unused")
public final class BroadcastEventBridgeImpl implements EventBridge {

    private static final String INTENT_PREFIX = "com.ironz.binaryprefs.";
    private static final String ACTION_PREFERENCE_UPDATED = INTENT_PREFIX + "ACTION_PREFERENCE_UPDATED";
    private static final String ACTION_PREFERENCE_REMOVED = INTENT_PREFIX + "ACTION_PREFERENCE_REMOVED";

    private static final String PREFERENCE_NAME = "preference_name";
    private static final String PREFERENCE_KEY = "preference_update_key";

    private final List<OnSharedPreferenceChangeListener> listeners = new CopyOnWriteArrayList<>();
    private final Handler handler = new Handler();

    private final Context context;
    private final String prefName;
    private final CacheProvider cacheProvider;
    private final FileAdapter fileAdapter;
    private final SerializerFactory serializerFactory;
    private final TaskExecutor taskExecutor;

    public BroadcastEventBridgeImpl(Context context,
                                    String prefName,
                                    CacheProvider cacheProvider,
                                    FileAdapter fileAdapter,
                                    SerializerFactory serializerFactory,
                                    TaskExecutor taskExecutor) {
        this.context = context;
        this.prefName = prefName;
        this.cacheProvider = cacheProvider;
        this.fileAdapter = fileAdapter;
        this.serializerFactory = serializerFactory;
        this.taskExecutor = taskExecutor;
        subscribeUpdateReceiver();
        subscribeRemoveReceiver();
    }

    private void subscribeUpdateReceiver() {
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notifyUpdate(intent);
            }
        }, new IntentFilter(ACTION_PREFERENCE_UPDATED));
    }

    private void subscribeRemoveReceiver() {
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notifyRemove(intent);
            }
        }, new IntentFilter(ACTION_PREFERENCE_REMOVED));
    }

    private void notifyUpdate(final Intent intent) {
        if (!prefName.equals(intent.getStringExtra(PREFERENCE_NAME))) {
            return;
        }
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                String key = intent.getStringExtra(PREFERENCE_KEY);
                byte[] bytes = fileAdapter.fetch(key);
                Object o = serializerFactory.deserialize(key, bytes);
                cacheProvider.put(key, o);
                notifyListeners(key);
            }
        });
    }

    private void notifyRemove(final Intent intent) {
        if (!prefName.equals(intent.getStringExtra(PREFERENCE_NAME))) {
            return;
        }
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                String key = intent.getStringExtra(PREFERENCE_KEY);
                cacheProvider.remove(key);
                notifyListeners(key);
            }
        });
    }

    private void notifyListeners(final String key) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (OnSharedPreferenceChangeListener listener : listeners) {
                    listener.onSharedPreferenceChanged(null, key);
                }
            }
        });
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListenersUpdate(Preferences preferences, String key, Object value) {
        Intent intent = new Intent(ACTION_PREFERENCE_UPDATED);
        intent.putExtra(PREFERENCE_NAME, prefName);
        intent.putExtra(PREFERENCE_KEY, key);
        context.sendBroadcast(intent);
    }

    @Override
    public void notifyListenersRemove(Preferences preferences, String key) {
        Intent intent = new Intent(ACTION_PREFERENCE_REMOVED);
        intent.putExtra(PREFERENCE_NAME, prefName);
        intent.putExtra(PREFERENCE_KEY, key);
        context.sendBroadcast(intent);
    }
}