package com.zero.support.xx.compat;


import android.app.Activity;
import android.app.ActivityThread;
import android.app.Application;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.excean.support.ActivityInjector;
import com.excean.support.util.SingleLiveEvent;
import com.excean.support.work.AppExecutor;
import com.excean.support.work.PromiseObservable;

import java.util.HashMap;
import java.util.Map;


public class InjectViewModel {
    private final Map<Class<?>, Dialog> dialogs = new HashMap<>();

    private final PromiseObservable<PermissionModel> permissionModels = new PromiseObservable<>();

    private final PromiseObservable<ActivityResultModel> resultModels = new PromiseObservable<>();

    private final PromiseObservable<DialogModel> dialogEvent = new PromiseObservable<>();

    private final PromiseObservable<Tip> tipsEvent = new PromiseObservable<>();

    private InjectFragment fragment;

    public LiveData<PermissionModel> obtainPermission() {
        return permissionModels.asLive();
    }

    public LiveData<ActivityResultModel> obtainActivityResult() {
        return resultModels.asLive();
    }

    public LiveData<DialogModel> obtainDialog() {
        return dialogEvent.asLive();
    }

    public LiveData<Tip> obtainTipEvent() {
        return tipsEvent.asLive();
    }

    void attachFragment(InjectFragment fragment) {
        this.fragment = fragment;
        onAttachFragment(fragment);
    }

    private void onAttachFragment(final InjectFragment fragment) {
        obtainPermission().observe(fragment, new Observer<PermissionModel>() {
            @Override
            public void onChanged(PermissionModel model) {
                if (model == null) {
                    return;
                }
                if (!model.isExecuted()) {
                    model.setExecuted(true);
                    PermissionHelper.requestPermission(InjectViewModel.this, 100, model.permissions());
                }
            }
        });
        obtainActivityResult().observe(fragment, new Observer<ActivityResultModel>() {
            @Override
            public void onChanged(ActivityResultModel model) {
                if (model == null) {
                    return;
                }
                if (!model.isExecuted()) {
                    model.setExecuted(true);
                    fragment.startActivityForResult(model.intent(), 100);
                }
            }
        });
        obtainDialog().observe(fragment, new Observer<DialogModel>() {
            @Override
            public void onChanged(DialogModel model) {
                if (model == null) {
                    return;
                }
                dispatchDialogEvent(model);
            }

        });
        obtainTipEvent().observe(fragment, new Observer<DialogModel>() {
            @Override
            public void onChanged(DialogModel model) {
                if (model == null) {
                    return;
                }
                dispatchDialogEvent(model);
            }
        });
        obtainMessageEvent().observe(fragment, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                ActivityInjector.sendMessage(s);
            }
        });
    }


    public InjectFragment getFragment() {
        return fragment;
    }


    Tip getCurrentTip() {
        return tipsEvent.getValue();
    }


    final void dispatchRequestPermission(String[] permissions, int[] grantResults) {
        PermissionModel model = peekPermissionModel();
        if (model != null) {
            model.dispatchResult(permissions, grantResults);
            removePermissionModel(model);
        }
    }

    final void dispatchRequestResult(int resultCode, Intent data) {
        ActivityResultModel model = peekActivityResultModel();
        if (model != null) {
            model.dispatchResult(resultCode, data);
            removeActivityResultModel(model);
        }
    }

    void removePermissionModel(PermissionModel model) {
        model.detach();
        permissionModels.remove(model);
    }

    public PermissionModel peekPermissionModel() {
        return permissionModels.getValue();
    }

    void removeActivityResultModel(ActivityResultModel model) {
        resultModels.remove(model);
    }

    public ActivityResultModel peekActivityResultModel() {
        return resultModels.getValue();
    }


    //*******************************************//

    private final SingleLiveEvent<String> messageEvent = new SingleLiveEvent<>();


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

    public LiveData<String> obtainMessageEvent() {
        return messageEvent;
    }


    public void tip(int type, String message) {
        requestTip(new Tip(type, message));
    }

    public void postTip(int type, String message) {
        final Tip tip = new Tip(type, message);
        if (AppExecutor.isMainThread()) {
            requestTip(tip);
        } else {
            AppExecutor.main().execute(new Runnable() {
                @Override
                public void run() {
                    requestTip(tip);
                }
            });
        }
    }

    public void postMessage(String message) {
        messageEvent.postValue(message);
    }

    public void postMessage(int id) {
        final Application application = getApplication();
        if (application != null) {
            postMessage(application.getString(id));
        }
    }

    private Application getApplication() {
        return ActivityThread.currentApplication();
    }

    public void postSuccess(int id) {
        final Application application = getApplication();
        if (application != null) {
            postSuccess(application.getString(id));
        }
    }

    public void postSuccess(String message) {
        postTip(Tip.TYPE_SUCCESS, message);
    }

    public void success(String message) {
        tip(Tip.TYPE_SUCCESS, message);
    }

    public void error(String message) {
        tip(Tip.TYPE_FAIL, message);
    }

    public void postError(int id) {
        final Application application = getApplication();
        if (application != null) {
            postError(application.getString(id));
        }
    }

    public void postError(String message) {
        postTip(Tip.TYPE_FAIL, message);
    }

    public void loading(String message) {
        tip(Tip.TYPE_LOADING, message);
    }

    public void postLoading(int id) {
        final Application application = getApplication();
        if (application != null) {
            postLoading(application.getString(id));
        }
    }

    public void postLoading(String message) {
        postTip(Tip.TYPE_LOADING, message);
    }

    public void loading() {
        tip(Tip.TYPE_LOADING, null);
    }

    public void dismiss() {
        assertMainThread("dismiss");
        Tip tip = getCurrentTip();
        if (tip != null) {
            tip.dismiss();
        }
    }

    public void postDismiss() {
        if (AppExecutor.isMainThread()) {
            dismiss();
        } else {
            AppExecutor.main().execute(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
        }
    }


    public PermissionModel requestPermission(PermissionModel model) {
        model.attach(this);
        permissionModels.setValue(model);
        return model;
    }

    public final Tip requestTip(Tip tip) {
        tip.attach(this);
        tipsEvent.setValue(tip);
        return tip;
    }

    public final DialogModel requestDialog(DialogModel model) {
        model.attach(this);
        dialogEvent.setValue(model);
        return model;
    }

    public final DialogModel requestDialogAtFront(DialogModel model){
        model.attach(this);
        dialogEvent.postAtFront(model);
        return model;
    }

    public final ActivityResultModel requestActivityResult(ActivityResultModel model) {
        model.attach(this);
        resultModels.setValue(model);
        return model;
    }

    public void removeDialog(DialogModel model) {
        if (!model.isEnableCached()) {
            dialogs.remove(model.getClass());
        }
        dialogEvent.remove(model);
        model.detach();
    }

    public void removeTip(Tip tip) {
        if (!tip.isEnableCached()) {
            dialogs.remove(tip.getClass());
        }
        tipsEvent.remove(tip);
    }


    public Activity requireActivity() {
        Fragment fragment = getFragment();
        if (fragment == null) {
            throw new RuntimeException("not attach");
        }
        return fragment.getActivity();
    }

    public final void detachContext() {
        fragment = null;
        for (Dialog dialog : dialogs.values()) {
            dialog.dismiss();
        }
    }


    protected void dispatchDialogEvent(DialogModel model) {
        Dialog dialog = dialogs.get(model.getClass());

        if (dialog == null) {
            dialog = model.onCreateDialog(requireActivity());
            if (dialog == null) {
                Log.e("support", "dispatchDialogEvent: fail for " + model);
                return;
            }
            dialogs.put(model.getClass(), dialog);
        }
        model.show(dialog);
    }

    public Activity getActivity() {
        Fragment fragment = getFragment();
        if (fragment == null) {
            return null;
        }
        return fragment.getActivity();
    }
}
