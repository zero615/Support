package com.zero.support.core.app;


import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.os.Looper;

import com.zero.support.core.AppGlobal;


public final class InjectViewModel {

    private InjectFragment fragment;
    private final InjectorHelper injectorHelper;
    private Injector injector;

    public InjectViewModel() {
        injectorHelper = new InjectorHelper(this);
    }

    public final Injector getCurrentInjector() {
        return injector;
    }

    public final void setCurrentInjector(Injector injector) {
        this.injector = injector;
    }

    public final void restoreInjectorContext(Injector context) {
        this.injector = context;
    }

    public InjectorHelper getInjectorHelper() {
        return injectorHelper;
    }

    final void attachFragment(InjectFragment fragment) {
        this.fragment = fragment;
        injectorHelper.onAttachFragment(this, fragment);
    }

    public final InjectFragment getFragment() {
        return fragment;
    }


    public final LayerModel<?> getCurrentWindowModel(int windowType) {
        return injectorHelper.getCurrentWindowModel(windowType);
    }


    public final PermissionModel peekPermissionModel() {
        return injectorHelper.permissionModels.getValue();
    }


    public final ActivityResultModel peekActivityResultModel() {
        return injectorHelper.resultModels.getValue();
    }

    private Object mValue;

    protected static void assertMainThread(String methodName) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Cannot invoke " + methodName + " on a background"
                    + " thread");
        }
    }


    public <T> T getValue() {
        return (T) mValue;
    }

    public void setValue(Object mValue) {
        this.mValue = mValue;
    }

    public void postMessage(Object message) {
        injectorHelper.messageEvent.postValue(message);
    }


    public Application getApplication() {
        return AppGlobal.currentApplication();
    }

    public void dismiss() {
        assertMainThread("dismiss");
        LayerModel<?> model = injectorHelper.getCurrentWindowModel(LayerModel.TYPE_SERIAL_NOTIFICATION);
        if (model != null) {
            model.dismiss();
        }
    }

    public PermissionModel requestPermission(PermissionModel model) {
        model.attach(this);
        injectorHelper.permissionModels.setValue(model);
        return model;
    }


    public final <T extends LayerModel<?>> T requestWindow(T model) {
        injectorHelper.requestWindow(model);
        return model;
    }

    public final <T extends LayerModel<?>> T requestWindowAtFront(T model) {
        injectorHelper.requestWindowAtFront(model);
        return model;
    }

    public final void removeWindow(LayerModel<?> model) {
        injectorHelper.removeWindow(model);
    }

    public interface WindowCreator {
        Object onCreateTarget(LayerModel<?> model, Activity activity);
    }

    public interface MessageDispatcher {
        boolean dispatchMessage(Object msg);
    }


    public final ActivityResultModel requestActivityResult(ActivityResultModel model) {
        model.attach(this);
        injectorHelper.resultModels.setValue(model);
        return model;
    }

    public Activity requireActivity() {
        Fragment fragment = getFragment();
        if (fragment == null) {
            throw new RuntimeException("not attach");
        }
        return fragment.getActivity();
    }

    public final void detachFragment() {
        injectorHelper.detachFragment();
        fragment = null;
    }


    public Activity getActivity() {
        Fragment fragment = getFragment();
        if (fragment == null) {
            return null;
        }
        return fragment.getActivity();
    }
}
