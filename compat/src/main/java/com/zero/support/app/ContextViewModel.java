package com.zero.support.app;

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

    void attachFragment(SupportFragment fragment) {
        this.activity = (SupportActivity) fragment.requireActivity();
        this.fragment = fragment;
        onAttachFragment(fragment);
    }

    void attachActivity(SupportActivity activity) {
        this.activity = activity;
        onAttachActivity(activity);
    }

    void detachContext() {
        onDetachContext();
        fragment = null;
        activity = null;
    }

    public boolean isFragment() {
        return fragment != null;
    }

    protected void onAttachFragment(SupportFragment fragment) {

    }


    protected void onAttachActivity(SupportActivity activity) {

    }

    protected void onDetachContext() {

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
