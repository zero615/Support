package com.zero.support.app;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;


import com.zero.support.core.app.ActivityResultEvent;
import com.zero.support.core.app.ActivityResultModel;
import com.zero.support.core.app.DialogClickEvent;
import com.zero.support.core.app.DialogModel;
import com.zero.support.core.app.InjectViewModel;
import com.zero.support.core.app.PermissionEvent;
import com.zero.support.core.app.PermissionHelper;
import com.zero.support.core.app.PermissionModel;
import com.zero.support.core.observable.Observer;

public class SimplePermissionModel extends PermissionModel {

    private final Builder builder;
    private final RationObserver rationObserver = new RationObserver();
    private final DeniedObserver deniedObserver = new DeniedObserver();

    private class RationObserver implements Observer<DialogClickEvent> {
        InjectViewModel viewModel;
        PermissionEvent event;


        public void bind(InjectViewModel viewModel,PermissionEvent event){
            this.viewModel = viewModel;
            this.event = event;
        }

        @Override
        public void onChanged(DialogClickEvent clickEvent) {
            if (clickEvent.isPositive()) {
                viewModel.requestPermission(SimplePermissionModel.this);
            } else {
                dispatchPermissionEvent(event);
            }
            clickEvent.dismiss();
        }
    }

    private class DeniedObserver implements Observer<DialogClickEvent> {
        InjectViewModel viewModel;
        PermissionEvent event;

        public void bind(InjectViewModel viewModel,PermissionEvent event){
            this.viewModel = viewModel;
            this.event = event;
        }


        @Override
        public void onChanged(DialogClickEvent clickEvent) {
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
            clickEvent.dismiss();
        }
    }

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
        final InjectViewModel viewModel = requireViewModel();
        boolean denied = event.isPermanentlyDenied();

        if (denied && builder.denied != null) {
            deniedObserver.bind(viewModel,event);
            viewModel.requestDialog(builder.denied).click().observe(deniedObserver);
        } else if (!denied && !event.isGranted() && builder.ration != null) {
            //非永久拒绝
            rationObserver.bind(viewModel,event);
            viewModel.requestDialog(builder.ration).click().observe(rationObserver);
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
