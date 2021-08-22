package com.zero.support.compat.util;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;


import com.zero.support.work.AppExecutor;
import com.zero.support.util.Observable;

import java.util.Set;

public class PreferenceObservable<T> extends Observable<T> implements Runnable {

    private final String key;
    private final Class<?> cls;
    private final T defaultValue;
    private final Preferences preferences;

    public PreferenceObservable(Preferences preferences, String key, @NonNull T defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.cls = defaultValue.getClass();
        this.defaultValue = defaultValue;
        AppExecutor.current().execute(this);
    }

    public PreferenceObservable(Preferences preferences, String key, Class<T> cls, T defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.cls = cls;
        this.defaultValue = defaultValue;
        AppExecutor.current().execute(this);
    }


    @Override
    public void setValue(T value) {
        updateValue(value);
        super.setValue(value);
    }


    public void updateValue(T value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (cls == Integer.class || cls == int.class) {
            editor.putInt(key, (Integer) value);
        } else if (cls == String.class) {
            editor.putString(key, (String) value);
        } else if (cls == Boolean.class || cls == boolean.class) {
            editor.putBoolean(key, (Boolean) value);
        } else if (cls == Float.class || cls == float.class) {
            editor.putFloat(key, (Float) value);
        } else if (cls == Long.class || cls == long.class) {
            editor.putLong(key, (Long) value);
        } else if (cls == Set.class) {
            editor.putStringSet(key, (Set<String>) value);
        }
        editor.apply();
    }

    @Override
    public void run() {
        Object value = null;
        if (cls == Integer.class || cls == int.class) {
            value = preferences.getInt(key, (Integer) defaultValue);
        } else if (cls == String.class) {
            value = preferences.getString(key, (String) defaultValue);
        } else if (cls == Boolean.class || cls == boolean.class) {
            value = preferences.getBoolean(key, (Boolean) defaultValue);
        } else if (cls == Float.class || cls == float.class) {
            value = preferences.getFloat(key, (Float) defaultValue);
        } else if (cls == Long.class || cls == long.class) {
            value = preferences.getLong(key, (Long) defaultValue);
        } else if (cls == Set.class) {
            value = preferences.getStringSet(key, (Set<String>) defaultValue);
        }
        if (value != null) {
            super.setValue((T) value);
        }
    }
}
