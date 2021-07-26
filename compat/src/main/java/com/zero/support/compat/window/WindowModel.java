package com.zero.support.compat.window;

import android.content.Context;

import androidx.databinding.BaseObservable;

public abstract class WindowModel extends BaseObservable {
    private BaseWindow window;

    final void detachWindow() {
        window = null;
    }

    final void attachWindow(BaseWindow window) {
        this.window = window;
    }

    public BaseWindow requireWindow() {
        return window;
    }

    public void dismiss() {
        FloatWindowManager.getDefault().dismiss(this);
    }

    public void show() {
        FloatWindowManager.getDefault().show(this);
    }

    public boolean isDismiss() {
        return true;
    }

    public boolean isSupportApp() {
        return true;
    }

    public boolean isSupportActivity() {
        return true;
    }

    public abstract BaseWindow onCreateWindow(Context context, boolean app);

    public String getWindowName() {
        return getClass().getSimpleName();
    }
}
