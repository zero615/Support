package com.zero.support.compat.app;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zero.support.compat.ActivityManager;

public abstract class CommonDialog extends Dialog {
    private DialogModel model;

    public CommonDialog(@NonNull Context context) {
        super(context);
    }

    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CommonDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ActivityManager.destroyWindow(getWindow().getDecorView().getWindowToken());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ActivityManager.createDialogWindow(getWindow().getDecorView().getWindowToken(), getDialogName());
    }

    @Override
    public void show() {
        super.show();

    }

    @Override
    public void dismiss() {
        super.dismiss();

    }

    public String getDialogName() {
        DialogModel model = requireModel();
        if (model != null) {
            String name = model.getDialogName();
            if (name != null) {
                return name;
            }
        }
        return getClass().getName();
    }

    public final void setCurrentDialogModel(DialogModel model) {
        if (this.model != null) {
            this.model.detachDialog(this);
            onUnBindDialogModel(this.model);
        }
        this.model = model;
        if (model != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create();
            }
            model.show(this);
            onBindDialogModel(model);
        }
    }

    public DialogModel requireModel() {
        return model;
    }

    protected void onBindDialogModel(DialogModel model) {

    }

    protected void onUnBindDialogModel(DialogModel model) {

    }

    protected abstract void performClicked(int which);

}
