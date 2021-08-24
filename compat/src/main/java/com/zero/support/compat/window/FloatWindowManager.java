package com.zero.support.compat.window;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.zero.support.compat.ActivityManager;
import com.zero.support.core.AppGlobal;
import com.zero.support.compat.util.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("ALL")
public class FloatWindowManager {
    private static final Singleton<FloatWindowManager> singleton = new Singleton<FloatWindowManager>() {
        @Override
        protected FloatWindowManager create() {
            return new FloatWindowManager();
        }
    };
    private WindowManager manager;
    private Application app;
    private Map<WindowModel, BaseWindow> appWindows = new HashMap<>();
    private Map<WindowModel, WeakHashMap<Activity, BaseWindow>> activityWindows = new HashMap<>();
    private List<WindowModel> models = new ArrayList<>();
    private boolean appMode = false;

    public FloatWindowManager() {
        app = AppGlobal.currentApplication();
        manager = (WindowManager) app.getSystemService(Context.WINDOW_SERVICE);
        app.registerActivityLifecycleCallbacks(new FloatActivityCallback());
    }

    public static FloatWindowManager getDefault() {
        return singleton.get();
    }

    private BaseWindow getAppWindow(WindowModel model) {
        BaseWindow window = appWindows.get(model);
        if (window == null) {
            window = model.onCreateWindow(app, true);
            appWindows.put(model, window);
        }
        return window;
    }

    private BaseWindow getActivityWindow(WindowModel model, Activity activity) {
        if (!model.isSupportActivity()) {
            return null;
        }
        WeakHashMap<Activity, BaseWindow> map = activityWindows.get(model);
        if (map == null) {
            map = new WeakHashMap<>();
            activityWindows.put(model, map);
        }
        BaseWindow window = map.get(activity);
        if (window == null) {
            window = model.onCreateWindow(activity, false);
            if (window != null) {
                map.put(activity, window);
            }
        }
        return window;
    }

    @SuppressWarnings("ALL")
    private void show(WindowModel model, Activity activity) {
        if (appMode) {
            return;
        }
        BaseWindow window = getActivityWindow(model, activity);
        if (window == null) {
            return;
        }
        model.attachWindow(window);
        window.show(model);
    }

    public void dismiss(WindowModel model) {
        dismissAppWindows(model);
        dismissActivityWindows(model);
        models.remove(model);
    }

    @SuppressWarnings("ALL")
    private void dismissAppWindows(WindowModel model) {
        BaseWindow window = appWindows.remove(model);
        if (window != null) {
            window.dismiss();
        }
    }

    private void dismissActivityWindows(WindowModel model) {
        WeakHashMap<Activity, BaseWindow> map = activityWindows.get(model);
        if (map == null) {
            return;
        }
        for (BaseWindow w : map.values()) {
            w.dismiss();
        }
        map.clear();
    }

    @SuppressWarnings("ALL")
    public void show(WindowModel model) {
        boolean useApp;
        models.add(model);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            useApp = Settings.canDrawOverlays(app);
        } else {
            useApp = true;
        }
        appMode = useApp;
        if (useApp) {
            if (model.isSupportApp()) {
                dismissActivityWindows(model);
                BaseWindow window = getAppWindow(model);
                if (window != null) {
                    window.show(model);
                }
            }
        } else {
            dismissAppWindows(model);
            Activity activity = ActivityManager.getFirstActivity();
            if (activity == null) {
                return;
            }
            if (model.isSupportActivity()) {
                show(model, activity);
            }
        }

    }

    @SuppressWarnings("ALL")
    private class FloatActivityCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            for (WindowModel model : models) {
                getActivityWindow(model, activity);
            }

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            for (WindowModel model : models) {
                if (model.isSupportActivity()) {
                    show(model, activity);
                }
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            for (WindowModel model : models) {
                BaseWindow window = getActivityWindow(model, activity);
                if (window == null) {
                    continue;
                }
                window.dismiss();
            }
        }
    }
}
