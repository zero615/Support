package com.zero.support.compat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.zero.support.app.ActivityInjector;
import com.zero.support.compat.observable.SingleLiveEvent;
import com.zero.support.compat.util.Preferences;
import com.zero.support.compat.util.SharedPreferenceObservable;
import com.zero.support.core.AppExecutor;
import com.zero.support.core.task.ObjectManager;

import java.io.File;

public class AppGlobal {
    private static final ObjectManager<String, SharedPreferenceObservable<?>> sharedPreferences = new ObjectManager<>();
    private static Application app;
    private static File preferenceDir;
    private final static SingleLiveEvent<String> message = new SingleLiveEvent<>();
    private static Toast toast;

    static {
        message.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (toast != null) {
                    toast.cancel();
                }
                if (s != null) {
                    toast = Toast.makeText(app, s, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private static final ObjectManager<String, Preferences> preferences = new ObjectManager<>(new ObjectManager.Creator<String, Preferences>() {
        @Override
        public Preferences creator(String key) {
            if (key.startsWith("/")) {
                return new Preferences(new File(key));
            }
            return new Preferences(new File(preferenceDir, key));
        }
    });
    private static final ObjectManager<String, Preferences> encryptPreferences = new ObjectManager<>(new ObjectManager.Creator<String, Preferences>() {
        @Override
        public Preferences creator(String key) {
            if (key.startsWith("/")) {
                return new Preferences(new File(key), true, true);
            }
            return new Preferences(new File(preferenceDir, key), true, true);
        }
    });

    public static void initialize(Application app) {
        AppGlobal.app = app;
        ActivityInjector.inject();
        app.registerActivityLifecycleCallbacks(ActivityManager.callbacks);
        AppGlobal.preferenceDir = new File(app.getFilesDir(), "preferences");
    }


    public static Application getApplication() {
        return app;
    }


    public static SharedPreferences sharedPreferences(String group) {
        return app.getSharedPreferences(group, Context.MODE_PRIVATE);
    }

    public static Preferences preferences(String group) {
        return preferences.opt(group);
    }

    public static Preferences encryptPreferences(String group) {
        return encryptPreferences.opt(group);
    }


    @SuppressWarnings("ALL")
    public static <T> SharedPreferenceObservable<T> sharedPreferences(String group, String key, T value) {
        String realKey = group + key;
        synchronized (sharedPreferences) {
            @SuppressWarnings("ALL")
            SharedPreferenceObservable<T> observable = (SharedPreferenceObservable<T>) sharedPreferences.get(realKey);
            if (observable == null) {
                Class cls;
                if (value == null) {
                    cls = String.class;
                } else {
                    cls = value.getClass();
                }
                observable = new SharedPreferenceObservable<>(group, key, cls, value);
                sharedPreferences.put(realKey, observable);
            }
            return observable;
        }
    }

    public static void sendMessage(String msg) {
        if (AppExecutor.isMainThread()) {
            message.setValue(msg);
        } else {
            message.postValue(msg);
        }
    }

    public static void cancelMessage() {
        sendMessage(null);
    }

}
