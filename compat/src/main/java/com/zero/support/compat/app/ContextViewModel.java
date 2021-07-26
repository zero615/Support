package com.zero.support.compat.app;

import android.annotation.SuppressLint;
import android.app.Application;

import com.zero.support.compat.AppGlobal;


public class ContextViewModel extends ObservableViewModel {
    private final Application application;
    @SuppressLint("StaticFieldLeak")
    private SupportActivity activity;
    private SupportFragment fragment;

    public ContextViewModel() {
        application = AppGlobal.getApplication();
    }

    void attach(SupportFragment fragment) {
        this.activity = (SupportActivity) fragment.requireActivity();
        this.fragment = fragment;
    }

    void attach(SupportActivity activity) {
        this.activity = activity;
    }

    public SupportFragment requireFragment() {
        final SupportFragment fragment = getFragment();
        if (fragment == null) {
            throw new IllegalStateException("Fragment " + this + " not attached to an activity.");
        }
        return fragment;
    }


    public SupportFragment getFragment() {
        return fragment;
    }

    public Application getApplication() {
        return application;
    }

    void detach() {
        fragment = null;
        activity = null;
    }

    public SupportActivity getActivity() {
        return activity;
    }

    public SupportActivity requireActivity() {
        if (activity == null) {
            throw new RuntimeException("activity is destroy");
        }
        return activity;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

    }
}
