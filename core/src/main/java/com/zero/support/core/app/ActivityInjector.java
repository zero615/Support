package com.zero.support.core.app;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.zero.support.core.AppGlobal;
import com.zero.support.core.observable.Observable;
import com.zero.support.core.observable.SingleLiveEvent;

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
            fragments.put(activity, createInjectFragment(activity));
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
    private final static SingleLiveEvent<Object> message = new SingleLiveEvent<>();

    static {
        AppGlobal.currentApplication().registerActivityLifecycleCallbacks(callbacks);
        message.observeForever(new Observer<Object>() {
            @Override
            public void onChanged(Object s) {
                if (toast != null) {
                    toast.cancel();
                }
                if (s == null) {
                    return;
                }
                toast = Toast.makeText(AppGlobal.currentApplication(), String.valueOf(s), Toast.LENGTH_SHORT);
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

    private static InjectFragment createInjectFragment(Activity activity) {
        InjectFragment fragment = (InjectFragment) activity.getFragmentManager().findFragmentByTag("ui-event-inject");
        if (fragment == null) {
            fragment = new InjectFragment();
            FragmentTransaction transaction = activity.getFragmentManager().beginTransaction()
                    .add(fragment, "ui-event-inject");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                transaction.commitNowAllowingStateLoss();
            } else {
                transaction.commitAllowingStateLoss();
            }
        }
        return fragment;
    }


    @MainThread
    public static InjectViewModel requireInjectViewModel(Activity activity) {
        InjectFragment fragment = fragments.get(activity);
        if (fragment == null) {
            throw new RuntimeException("activity is destroy");
        }
        return fragment.getInjectViewModel();
    }
}
