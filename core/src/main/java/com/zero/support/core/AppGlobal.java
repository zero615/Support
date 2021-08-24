package com.zero.support.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.zero.support.core.app.ActivityInjector;
import com.zero.support.core.observable.SingleLiveEvent;
import com.zero.support.core.task.ObjectManager;
import com.zero.support.core.util.Preferences;

import java.io.File;

public class AppGlobal {
    private static Application app;
    private static File preferenceDir;
    private final static SingleLiveEvent<Object> message = new SingleLiveEvent<>();
    private static Toast toast;

    static {
        message.observeForever(new Observer<Object>() {
            @Override
            public void onChanged(Object s) {
                if (toast != null) {
                    toast.cancel();
                }
                if (s != null) {
                    toast = Toast.makeText(app, String.valueOf(s), Toast.LENGTH_SHORT);
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

    public static void inject(Application app) {
        if (AppGlobal.app != app) {
            AppGlobal.app = app;
            ActivityInjector.inject();
            AppGlobal.preferenceDir = new File(app.getFilesDir(), "preferences");
        }
    }


    public static Application currentApplication() {
        return app;
    }


    public static SharedPreferences sharedPreferences(String group) {
        return app.getSharedPreferences(group, Context.MODE_PRIVATE);
    }

    public static Preferences preferences(String group) {
        return preferences.opt(group);
    }


    public static File getPreferenceDir() {
        return preferenceDir;
    }

    public static void sendMessage(Object msg) {
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
