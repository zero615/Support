package com.zero.support.compat.app;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.zero.support.work.PromiseObservable;


/**
 * Permission
 */
public class RequestViewModel extends SupportViewModel {
    private final PromiseObservable<PermissionModel> permissionModels = new PromiseObservable<>();

    private final PromiseObservable<ActivityResultModel> resultModels = new PromiseObservable<>();

    private final PromiseObservable<DialogModel> dialogEvent = new PromiseObservable<>();

    public LiveData<PermissionModel> obtainPermission() {
        return permissionModels.asLive();
    }

    public LiveData<ActivityResultModel> obtainActivityResult() {
        return resultModels.asLive();
    }

    public LiveData<DialogModel> obtainDialog() {
        return dialogEvent.asLive();
    }

    final <T extends DialogModel> T performRequestDialog(@NonNull T model) {
        model.attach(this);
        dialogEvent.setValue(model);
        return model;
    }


    void removeDialogModel(DialogModel model) {
        SupportActivity activity = requireActivity();
        activity.requestRemoveDialog(model);
        dialogEvent.remove(model);
        model.detach();
    }

    final PermissionModel performRequestPermission(PermissionModel model) {
        model.attach(this);
        permissionModels.setValue(model);
        return model;
    }

    final ActivityResultModel performRequestActivityResult(ActivityResultModel model) {
        model.attach(this);
        resultModels.setValue(model);
        return model;
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

}
