package com.zero.support.compat.util;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Preferences implements SharedPreferences {
    private static final String TAG = "vp_prefs";
    private static final boolean LOG = false;
    private static final String KEY_INIT = "safe_init";
    private File file;
    private long lastModified;
    private JSONObject mObject = new JSONObject();
    private LockHelper mLockHelper;
    private boolean encrypt;

    public Preferences(File file, boolean init, boolean encrypt) {
        this.file = file;
        this.encrypt = encrypt;
        if (init) {
            synchronized (this) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        inflateInternalLock();
                    }
                }).start();
            }
        }

    }

    public Preferences(File file) {
        this(file, true, false);
    }

    public synchronized boolean isEmpty() {
        inflateInternalLock();
        return mObject.length() == 0;
    }

    @Override
    public synchronized String toString() {
        inflateInternalLock();
        return mObject.toString();
    }

    private synchronized void writeLauncherLock(JSONObject object) {
        lock();
        try {
            file.getParentFile().mkdirs();
            writeLauncherInternal(file, object);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release();
        }
    }

    public synchronized void inflateInternalLock() {
        long modifyTime = file.lastModified();
        if (lastModified == modifyTime) {
            return;
        }

        final File file = this.file;
        file.getParentFile().mkdirs();
        lock();
        try {
            String json = readFromFile(file);
            if (LOG) {
                Log.d(TAG, "inflateInternalLock: " + json);
            }
            if (TextUtils.isEmpty(json)) {
                mObject = new JSONObject();
            } else {
                mObject = new JSONObject(json);
            }
            lastModified = modifyTime;
        } catch (Exception e) {
            e.printStackTrace();
            if (mObject == null) {
                mObject = new JSONObject();
                lastModified = modifyTime;
            }
        } finally {
            release();
        }
    }

    public String readFromFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        FileReader reader = new FileReader(file);
        StringBuilder builder = new StringBuilder();
        char[] buf = new char[1024];
        int count = 0;
        while ((count = reader.read(buf)) != -1) {
            builder.append(buf, 0, count);
        }
        reader.close();
        return builder.toString();
    }

    private void writeLauncherInternal(File file, JSONObject launcher) {
        File tmpFile = new File(file.getParentFile(), file.getName() + ".tmp");
        try {
            file.getParentFile().mkdirs();
            tmpFile.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(tmpFile);
            writer.write(launcher.toString());
            writer.close();
            tmpFile.renameTo(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized JSONObject getJSONObject() {
        inflateInternalLock();
        return mObject;
    }

    @Override
    public synchronized Map<String, Object> getAll() {
        inflateInternalLock();
        Map<String, Object> map = new HashMap<String, Object>(mObject.length());
        Iterator<String> iterator = mObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            map.put(key, mObject.opt(key));
        }
        return map;
    }

    @Override
    public synchronized String getString(String key, String defValue) {
        inflateInternalLock();
        return mObject.optString(key, defValue);
    }

    @Override
    public synchronized Set<String> getStringSet(String key, Set<String> defValues) {
        inflateInternalLock();
        JSONArray array = mObject.optJSONArray(key);
        if (array != null) {
            Set<String> strings = new LinkedHashSet<String>(array.length());
            for (int i = 0; i < array.length(); i++) {
                strings.add(array.optString(i));
            }
            return strings;
        } else {
            return defValues;
        }
    }

    @Override
    public synchronized int getInt(String key, int defValue) {
        inflateInternalLock();
        return mObject.optInt(key, defValue);
    }

    @Override
    public synchronized long getLong(String key, long defValue) {
        inflateInternalLock();
        return mObject.optLong(key, defValue);
    }

    @Override
    public synchronized float getFloat(String key, float defValue) {
        inflateInternalLock();
        return (float) mObject.optDouble(key, defValue);
    }

    @Override
    public synchronized boolean getBoolean(String key, boolean defValue) {
        inflateInternalLock();
        return mObject.optBoolean(key, defValue);
    }


    @Override
    public boolean contains(String key) {
        inflateInternalLock();
        return mObject.has(key);
    }

    @Override
    public Editor edit() {
        return new SyncEditor();
    }


    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }


    public synchronized boolean commitToMemory(Map<String, Object> modifies, Set<String> deleted, boolean clear) {
        lock();
        JSONObject object = null;
        if (clear) {
            object = new JSONObject();
        } else {
            Map<String, Object> map = getAll();
            object = new JSONObject(map);
        }
        try {
            for (String key : modifies.keySet()) {
                Object value = modifies.get(key);
                if (value instanceof Set) {
                    object.put(key, new JSONArray(((Set) value).toArray()));
                } else {
                    object.put(key, value);
                }

            }
            for (String key : deleted) {
                object.remove(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        writeLauncherLock(object);
        lastModified = file.lastModified();
        mObject = object;
        release();
        return true;
    }

    public synchronized void writeUnSafe(String json) {
        lock();
        try {
            JSONObject object = new JSONObject(json);
            writeUnSafe(object);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            release();
        }
    }

    public synchronized void writeUnSafe(JSONObject object) {
        lock();
        try {
            writeLauncherLock(object);
            lastModified = file.lastModified();
            mObject = object;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release();
        }
    }

    public synchronized void lock() {
        if (mLockHelper == null) {
            mLockHelper = LockHelper.getLock(file);
        } else {
            mLockHelper.lock();
        }
    }

    public synchronized void release() {
        if (mLockHelper != null) {
            if (mLockHelper.release()) {
                mLockHelper = null;
            }
        }
    }


    public boolean putBoolean(String key, boolean value) {
        return edit().putBoolean(key, value).commit();
    }

    public boolean putInt(String key, int value) {
        return edit().putInt(key, value).commit();
    }

    public boolean putLong(String key, long value) {
        return edit().putLong(key, value).commit();
    }

    public boolean putString(String key, String value) {
        return edit().putString(key, value).commit();
    }


    private static class LockHelper {
        RandomAccessFile mAccessFile;
        FileLock mFileLock;
        AtomicInteger mCount = new AtomicInteger(1);

        public LockHelper(File file) {
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                mAccessFile = new RandomAccessFile(file, "rw");
                mFileLock = mAccessFile.getChannel().lock();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public static LockHelper getLock(File file) {
            return new LockHelper(file);
        }

        public void lock() {
            mCount.addAndGet(1);
        }

        public boolean release() {
            int count = mCount.decrementAndGet();
            if (count > 0) {
                return false;
            }
            try {
                mFileLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mAccessFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private class SyncEditor implements Editor {
        Map<String, Object> mModifies;
        Set<String> mDeleted;
        boolean clear;


        public SyncEditor() {
            mModifies = new HashMap<String, Object>();
            mDeleted = new HashSet<String>();
        }


        @Override
        public Editor putString(String key, String value) {
            if (value == null) {
                mDeleted.add(key);
            } else {
                mDeleted.remove(key);
                mModifies.put(key, value);
            }
            return this;
        }

        @Override
        public Editor putStringSet(String key, Set<String> values) {
            if (values == null) {
                mDeleted.add(key);
            } else {
                mDeleted.remove(key);
                mModifies.put(key, values);
            }
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            mDeleted.remove(key);
            mModifies.put(key, value);
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            mDeleted.remove(key);
            mModifies.put(key, value);
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            mDeleted.remove(key);
            mModifies.put(key, value);
            return this;
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            mDeleted.remove(key);
            mModifies.put(key, value);
            return this;
        }

        @Override
        public Editor remove(String key) {
            mModifies.remove(key);
            mDeleted.add(key);
            return this;
        }

        @Override
        public Editor clear() {
            mDeleted.addAll(mModifies.keySet());
            clear = true;
            return this;
        }

        @Override
        public boolean commit() {
            return commitToMemory(mModifies, mDeleted, clear);
        }

        @Override
        public void apply() {
            commitToMemory(mModifies, mDeleted, clear);
        }


    }

}
