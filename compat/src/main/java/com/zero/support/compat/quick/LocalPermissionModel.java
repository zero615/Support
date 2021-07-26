package com.zero.support.compat.quick;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.zero.support.compat.app.ActivityResultEvent;
import com.zero.support.compat.app.ActivityResultModel;
import com.zero.support.compat.app.DialogClickEvent;
import com.zero.support.compat.app.PermissionEvent;
import com.zero.support.compat.app.PermissionModel;
import com.zero.support.compat.app.SupportViewModel;
import com.zero.support.work.Observer;

public class LocalPermissionModel extends PermissionModel {
    private String message;
    private int messageId;

    public LocalPermissionModel(String... permissions) {
        super(permissions);
    }

    public LocalPermissionModel content(String permission) {
        this.message = permission;
        return this;
    }

    public LocalPermissionModel content(int permission) {
        this.messageId = permission;
        return this;
    }

    @Override
    protected void onReceivePermissionEvent(final PermissionEvent event) {
        final SupportViewModel viewModel = requireViewModel();
        if (event.isPermanentlyDenied()) {
            if (message == null && messageId != 0) {
                message = requireViewModel().requireActivity().getResources().getString(messageId);
            }
            requireViewModel().requestDialog(new SimpleDialogModel.Builder()
                    .content(message).build()).click().observe(new Observer<DialogClickEvent>() {
                @Override
                public void onChanged(DialogClickEvent dialogClickEvent) {
                    dialogClickEvent.dismiss();
                    if (dialogClickEvent.isPositive()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", viewModel.getApplication().getPackageName(), null));
                        viewModel.requestActivityResult(new ActivityResultModel(intent)).result().observe(new Observer<ActivityResultEvent>() {
                            @Override
                            public void onChanged(ActivityResultEvent activityResultEvent) {
                                viewModel.requestPermission(LocalPermissionModel.this);
                            }
                        });
                    } else {
                        deliveryEvent(event);
                    }
                }
            });
        } else if (!event.isGranted()) {
            if (message == null && messageId != 0) {
                message = requireViewModel().requireActivity().getResources().getString(messageId);
            }
            requireViewModel().requestDialog(new SimpleDialogModel.Builder()
                    .content(message).build()).click().observe(new Observer<DialogClickEvent>() {
                @Override
                public void onChanged(DialogClickEvent dialogClickEvent) {
                    dialogClickEvent.dismiss();
                    if (dialogClickEvent.isPositive()) {
                        requireViewModel().requestPermission(LocalPermissionModel.this);
                    } else {
                        deliveryEvent(event);
                    }
                }
            });
        } else {
            deliveryEvent(event);
        }
    }
}
