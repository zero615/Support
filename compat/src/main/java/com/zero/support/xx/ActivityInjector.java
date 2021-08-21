package com.zero.support.xx;

import android.app.Activity;
import android.app.ActivityThread;
import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.excean.support.compat.InjectFragment;
import com.excean.support.compat.InjectViewModel;
import com.excean.support.util.SingleLiveEvent;
import com.excean.support.work.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityInjector {
    private static final List<Activity> activities = new ArrayList<>();
    private final static Map<Activity, InjectFragment> fragments = new HashMap<>();

    private final static Observable<Activity> mTopActivity = new Observable<>();
    static final Application.ActivityLifecycleCallbacks callbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            activities.add(0, activity);
            InjectFragment fragment = new InjectFragment();
            activity.getFragmentManager().beginTransaction()
                    .add(fragment, "ui-event-inject")
                    .commit();
            fragments.put(activity, fragment);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            mTopActivity.setValue(activity);
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            if (mTopActivity.getValue() == activity) {
                mTopActivity.setValue(null);
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            activities.remove(activity);
            fragments.remove(activity);
        }
    };
    static Toast toast;
    private final static SingleLiveEvent<String> message = new SingleLiveEvent<>();

    static {
        ActivityThread.currentApplication().registerActivityLifecycleCallbacks(callbacks);
        message.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(ActivityThread.currentApplication(), s, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @MainThread
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    @MainThread
    public static Activity getFirstActivity() {
        for (Activity activity : activities) {
            if (activity.isFinishing()) {
                continue;
            }
            return activity;
        }
        return null;
    }

    public static InjectViewModel getTopInjectViewModel() {
        Activity activity = getTopActivity();
        if (activity == null) {
            return null;
        }
        return getInjectViewModel(activity);
    }


    @MainThread
    public static Activity getTopActivity() {
        return mTopActivity.getValue();
    }

    @MainThread
    public static Observable<Activity> getTopObservable() {
        return mTopActivity;
    }

    /**
     * 初始化入口，第一个Activity 启动之前调用
     */
    public static void inject() {

    }

    @MainThread
    public static InjectViewModel getInjectViewModel(Activity activity) {
        InjectFragment fragment = fragments.get(activity);
        if (fragment == null) {
            return null;
        }
        return fragment.getInjectViewModel();
    }

    @MainThread
    public static InjectViewModel requireInjectViewModel(Activity activity) {
        InjectFragment fragment = fragments.get(activity);
        if (fragment == null) {
            throw new RuntimeException("activity is destroy");
        }
        return fragment.getInjectViewModel();
    }


    public static void sendMessage(String message) {
        ActivityInjector.message.postValue(message);
    }
}
