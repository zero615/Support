package com.zero.support.core.observable;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;


import com.zero.support.core.AppGlobal;
import com.zero.support.core.AppExecutor;
import com.zero.support.core.observable.Observable;

import java.util.Set;

public class SharedPreferenceObservable<T> extends Observable<T> implements Runnable {
    private final String group;
    private final String key;
    private final Class<?> cls;
    private final T defaultValue;

    public SharedPreferenceObservable(String group, String key, Class<T> cls, T defaultValue) {
        this.group = group;
        this.key = key;
        this.cls = cls;
        this.defaultValue = defaultValue;
        AppExecutor.current().execute(this);
    }

    public SharedPreferenceObservable(String group, String key, @NonNull T defaultValue) {
        this.group = group;
        this.key = key;
        this.cls = defaultValue.getClass();
        this.defaultValue = defaultValue;
        AppExecutor.current().execute(this);
    }


    @Override
    public void setValue(T value) {
        updateValue(value);
        super.setValue(value);
    }


    public void updateValue(T value) {
        SharedPreferences.Editor editor = AppGlobal.sharedPreferences(group).edit();
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
        } else if (Set.class.isAssignableFrom(cls)) {
            editor.putStringSet(key, (Set<String>) value);
        }
        editor.apply();
    }

    @Override
    public void run() {
        SharedPreferences preferences = AppGlobal.sharedPreferences(group);
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
        } else if (Set.class.isAssignableFrom(cls)) {
            value = preferences.getStringSet(key, (Set<String>) defaultValue);
        }
        if (value != null) {
            super.setValue((T) value);
        }
    }
}
