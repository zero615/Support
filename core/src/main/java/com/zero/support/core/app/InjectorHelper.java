package com.zero.support.core.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;


import com.zero.support.core.AppGlobal;
import com.zero.support.core.observable.PromiseObservable;
import com.zero.support.core.observable.SingleLiveEvent;


import java.util.HashMap;
import java.util.Map;

public class InjectorHelper {
    final Map<Class<?>, Dialog> dialogs = new HashMap<>();

    final PromiseObservable<PermissionModel> permissionModels = new PromiseObservable<>();

    final PromiseObservable<ActivityResultModel> resultModels = new PromiseObservable<>();

    final PromiseObservable<DialogModel> dialogEvent = new PromiseObservable<>();

    final PromiseObservable<Tip> tipsEvent = new PromiseObservable<>();

    final SingleLiveEvent<Object> messageEvent = new SingleLiveEvent<>();

    private InjectFragment fragment;

    public void onAttachFragment(final InjectViewModel viewModel, final InjectFragment fragment) {
        this.fragment = fragment;
        permissionModels.asLive().observe(fragment, new Observer<PermissionModel>() {
            @Override
            public void onChanged(PermissionModel model) {
                if (model == null) {
                    return;
                }
                if (!model.isExecuted()) {
                    model.setExecuted(true);
                    PermissionHelper.requestPermission(viewModel, 100, model.permissions());
                }
            }
        });
        resultModels.asLive().observe(fragment, new Observer<ActivityResultModel>() {
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
        dialogEvent.asLive().observe(fragment, new Observer<DialogModel>() {
            @Override
            public void onChanged(DialogModel model) {
                if (model == null) {
                    return;
                }
                dispatchDialogEvent(model);
            }

        });
        tipsEvent.asLive().observe(fragment, new Observer<DialogModel>() {
            @Override
            public void onChanged(DialogModel model) {
                if (model == null) {
                    return;
                }
                dispatchDialogEvent(model);
            }
        });
        messageEvent.observe(fragment, new Observer<Object>() {
            @Override
            public void onChanged(@Nullable Object msg) {
                Activity activity = viewModel.requireActivity();
                boolean handled = false;
                Injector injector = fragment.getInjectViewModel().getCurrentInjector();
                if (injector != null && activity != injector) {
                    handled = injector.dispatchMessage(msg);
                }
                if (activity instanceof InjectViewModel.MessageDispatcher) {
                    handled = ((InjectViewModel.MessageDispatcher) activity).dispatchMessage(msg);
                }
                if (!handled) {
                    AppGlobal.sendMessage(msg);
                }
            }
        });
    }


    protected void dispatchDialogEvent(DialogModel model) {
        Dialog dialog = dialogs.get(model.getClass());
        Activity activity = fragment.getActivity();
        Injector injector = fragment.getInjectViewModel().getCurrentInjector();
        if (injector != null && activity != injector) {
            dialog = injector.onCreateDialog(model, activity);
        }
        if (activity instanceof InjectViewModel.DialogCreator) {
            dialog = ((InjectViewModel.DialogCreator) activity).onCreateDialog(model, activity);
        }
        if (dialog == null) {
            dialog = model.onCreateDialog(fragment.getActivity());
            if (dialog == null) {
                Log.e("support", "dispatchDialogEvent: fail for " + model);
                return;
            }
            dialogs.put(model.getClass(), dialog);
        }
        model.show(dialog);
    }


    final void dispatchRequestPermission(String[] permissions, int[] grantResults) {
        PermissionModel model = permissionModels.getValue();
        if (model != null) {
            model.dispatchResult(permissions, grantResults);
            model.detach();
            permissionModels.remove(model);
        }
    }

    final void dispatchRequestResult(int resultCode, Intent data) {
        ActivityResultModel model = resultModels.getValue();
        if (model != null) {
            model.dispatchResult(resultCode, data);
            model.detach();
            resultModels.remove(model);
        }
    }

    void detachFragment() {
        for (Dialog dialog : dialogs.values()) {
            dialog.dismiss();
        }
    }
}
