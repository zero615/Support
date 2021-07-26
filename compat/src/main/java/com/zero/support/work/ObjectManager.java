package com.zero.support.work;

import java.util.HashMap;
import java.util.Map;


public class ObjectManager<K, V> {

    private final Map<K, V> objects = new HashMap<>();
    protected Creator<K, V> creator;


    public ObjectManager(Creator<K, V> creator) {
        this.creator = creator;
    }

    public ObjectManager() {
    }

    public V get(K key) {
        synchronized (objects) {
            return objects.get(key);
        }
    }

    public V opt(K key, Object extra) {
        synchronized (objects) {
            V value = objects.get(key);
            if (value == null) {
                value = onCreateValue(key);
                if (value != null) {
                    objects.put(key, value);
                }
            }
            onBindValue(value, extra);
            return value;
        }
    }

    protected void onBindValue(V value, Object extra) {

    }

    public V opt(K key) {
        synchronized (objects) {
            V value = objects.get(key);
            if (value == null) {
                value = onCreateValue(key);
                if (value != null) {
                    objects.put(key, value);
                }
            }
            onBindValue(value, null);
            return value;
        }
    }

    protected V onCreateValue(K key) {
        if (creator != null) {
            return creator.creator(key);
        }
        return null;
    }

    public V put(K key, V value) {
        synchronized (objects) {
            V result = objects.put(key, value);
            return result;
        }
    }

    public V remove(K key) {
        synchronized (objects) {
            return objects.remove(key);
        }
    }

    @Deprecated
    public V query(K key) {
        synchronized (objects) {
            return objects.get(key);
        }
    }

    @Deprecated
    public V insert(K key, V newValue) {
        synchronized (objects) {
            V result = objects.put(key, newValue);
            return result;
        }
    }

    @Deprecated
    public V delete(K key) {
        synchronized (objects) {
            return objects.remove(key);
        }
    }

    public interface Creator<K, V> {
        V creator(K key);
    }
}
