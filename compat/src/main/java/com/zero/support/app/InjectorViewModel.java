package com.zero.support.app;


import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Looper;

import com.zero.support.compat.AppGlobal;


public class InjectorViewModel {

    private InjectFragment fragment;
    private final InjectorHelper injectorHelper = new InjectorHelper();
    private Injector injector;

    public final Injector getCurrentInjector() {
        return injector;
    }

    public void setCurrentInjector(Injector injector) {
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


    public final Tip getCurrentTip() {
        return injectorHelper.tipsEvent.getValue();
    }


    public PermissionModel peekPermissionModel() {
        return injectorHelper.permissionModels.getValue();
    }


    public ActivityResultModel peekActivityResultModel() {
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
        return AppGlobal.getApplication();
    }

    public void dismiss() {
        assertMainThread("dismiss");
        Tip tip = getCurrentTip();
        if (tip != null) {
            tip.dismiss();
        }
    }

    public PermissionModel requestPermission(PermissionModel model) {
        model.attach(this);
        injectorHelper.permissionModels.setValue(model);
        return model;
    }

    public final void requestTip(Tip tip) {
        Tip topTip = getCurrentTip();
        if (topTip != null) {
            topTip.dismiss();
        }
        tip.attach(this);
        injectorHelper.tipsEvent.setValue(tip);
    }

    public interface DialogCreator {
        Dialog onCreateDialog(DialogModel model, Activity activity);
    }

    public interface MessageDispatcher {
        boolean dispatchMessage(Object msg);
    }


    public final DialogModel requestDialog(DialogModel model) {
        model.attach(this);
        injectorHelper.dialogEvent.setValue(model);
        return model;
    }

    public final DialogModel requestDialogAtFront(DialogModel model) {
        model.attach(this);
        injectorHelper.dialogEvent.postAtFront(model);
        return model;
    }

    public final ActivityResultModel requestActivityResult(ActivityResultModel model) {
        model.attach(this);
        injectorHelper.resultModels.setValue(model);
        return model;
    }

    public void removeDialog(DialogModel model) {
        if (!model.isEnableCached()) {
            injectorHelper.dialogs.remove(model.getClass());
        }
        injectorHelper.dialogEvent.remove(model);
        model.detach();
    }

    public void removeTip(Tip tip) {
        if (!tip.isEnableCached()) {
            injectorHelper.dialogs.remove(tip.getClass());
        }
        injectorHelper.tipsEvent.remove(tip);
        tip.detach();
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
