package com.zero.support.compat.quick;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.zero.support.compat.app.ActivityResultEvent;
import com.zero.support.compat.app.ActivityResultModel;
import com.zero.support.compat.app.DialogClickEvent;
import com.zero.support.compat.app.DialogModel;
import com.zero.support.compat.app.PermissionEvent;
import com.zero.support.compat.app.PermissionHelper;
import com.zero.support.compat.app.PermissionModel;
import com.zero.support.compat.app.SupportViewModel;
import com.zero.support.work.Observer;

public class SimplePermissionModel extends PermissionModel {

    private final Builder builder;

    public SimplePermissionModel(Builder builder) {
        super(builder.permissions);
        if (builder.denied == null) {
            builder.denied = builder.ration;
        }
        if (builder.ration == null) {
            builder.ration = builder.denied;
        }
        this.builder = builder;
    }


    @Override
    protected void onReceivePermissionEvent(final PermissionEvent event) {
        final SupportViewModel viewModel = requireViewModel();
        boolean denied = event.isPermanentlyDenied();
        if (denied && builder.denied != null) {
            viewModel.requestDialog(builder.denied).click().observe(new Observer<DialogClickEvent>() {
                @Override
                public void onChanged(DialogClickEvent clickEvent) {
                    clickEvent.dismiss();
                    if (clickEvent.isPositive()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", viewModel.getApplication().getPackageName(), null));
                        viewModel.requestActivityResult(new ActivityResultModel(intent)).result().observe(new Observer<ActivityResultEvent>() {
                            @Override
                            public void onChanged(ActivityResultEvent activityResultEvent) {
                                if (PermissionHelper.hasPermissions(viewModel.requireActivity(), event.getPermissions())) {
                                    viewModel.requestPermission(SimplePermissionModel.this);
                                }
                            }
                        });
                    } else {
                        dispatchPermissionEvent(event);
                    }
                }
            });
        } else if (!denied && !event.isGranted() && builder.ration != null) {
            //非永久拒绝
            requireViewModel().requestDialog(builder.ration).click().observe(new Observer<DialogClickEvent>() {
                @Override
                public void onChanged(DialogClickEvent clickEvent) {
                    clickEvent.dismiss();
                    if (clickEvent.isPositive()) {
                        requireViewModel().requestPermission(SimplePermissionModel.this);
                    } else {
                        dispatchPermissionEvent(event);
                    }
                }
            });
        } else {
            dispatchPermissionEvent(event);
        }
    }

    public static class Builder {
        DialogModel ration;
        DialogModel denied;
        private String[] permissions;

        public Builder permissions(String... permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder ration(String content) {
            return ration(new SimpleDialogModel.Builder()
                    .negative("取消")
                    .positive("确认")
                    .content(content).build());
        }

        public Builder ration(int content) {
            return ration(new SimpleDialogModel.Builder()
                    .negative("取消")
                    .positive("确认")
                    .content(content).build());
        }

        public Builder ration(DialogModel model) {
            this.ration = model;
            return this;
        }

        public Builder denied(DialogModel model) {
            this.denied = model;
            return this;
        }

        public Builder denied(String content) {
            return denied(new SimpleDialogModel.Builder()
                    .negative("取消")
                    .positive("确认")
                    .content(content).build());
        }

        public Builder denied(int content) {
            return denied(new SimpleDialogModel.Builder()
                    .negative("取消")
                    .positive("确认").content(content).build());
        }

        public PermissionModel build() {
            return new SimplePermissionModel(this);
        }
    }
}
