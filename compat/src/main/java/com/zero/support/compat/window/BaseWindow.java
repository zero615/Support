package com.zero.support.compat.window;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.zero.support.compat.ActivityManager;


public class BaseWindow implements LifecycleOwner {
    private static final String TAG = "window";
    @SuppressLint("StaticFieldLeak")
    private final LifecycleRegistry registry;
    WindowManager windowManager;
    WindowManager.LayoutParams params;
    private boolean create;
    private View contentView;
    private ViewGroup root;
    private WindowModel model;
    private boolean showing;
    private Context context;

    public BaseWindow(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = createDefaultLayoutParams(isAppWindow());
        this.root = new WindowParentLayout(context, this);
        registry = new LifecycleRegistry(this);
    }

    public static WindowManager.LayoutParams createDefaultLayoutParams(boolean app) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        if (!app) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
        }

        return params;
    }

    protected void onDetachedFromWindow() {
        ActivityManager.destroyWindow(getRoot().getWindowToken());
    }

    protected void onAttachedToWindow() {
        ActivityManager.createAppWindow(getRoot().getWindowToken(), getWindowName());
    }

    public Context getContext() {
        return context;
    }

    public <T extends WindowModel> T requireModel() {
        return (T) model;
    }

    protected void onCreate() {

    }

    private void create() {
        if (create) {
            return;
        }
        create = true;
        dispatchCreate();
    }

    private void dispatchCreate() {
        onCreate();
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View view) {
        this.contentView = view;
        root.addView(view);
    }

    public ViewGroup getRoot() {
        return root;
    }

    public boolean isShowing() {
        return showing;
    }

    void show(WindowModel model) {
        this.model = model;
        if (!showing) {
            model.attachWindow(this);
            create();
            registry.setCurrentState(Lifecycle.State.CREATED);
            registry.setCurrentState(Lifecycle.State.STARTED);
            registry.setCurrentState(Lifecycle.State.RESUMED);
            showing = true;
            windowManager.addView(getRoot(), getLayoutParams());
            ActivityManager.onCreateWindow(this);
        }
    }

    public void update() {
        if (showing) {
            windowManager.updateViewLayout(getRoot(), params);
        } else {
            Log.w(TAG, "update: " + showing + getContext());
        }

    }

    public void dismiss() {
        if (!showing) {
            return;
        }
        showing = false;
        if (model != null) {
            model.detachWindow();
            windowManager.removeView(getRoot());
            registry.setCurrentState(Lifecycle.State.DESTROYED);
            ActivityManager.onDismissWindow(this);
        }
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return params;
    }

    public boolean isAppWindow() {
        return !(getContext() instanceof Activity);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }

    public void onClickEvent(MotionEvent event) {

    }

    public boolean isRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        int rawX = (int) ev.getRawX();
        int rawY = (int) ev.getRawY();
        return rawX >= x && rawX <= (x + view.getWidth()) && rawY >= y && rawY <= (y + view.getHeight());
    }

    public String getWindowName() {
        WindowModel model = requireModel();
        if (model != null) {
            String name = model.getWindowName();
            if (name != null) {
                return name;
            }
        }
        return getClass().getName();
    }
}
