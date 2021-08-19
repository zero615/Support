package com.zero.support.compat.app;

import android.app.Dialog;
import android.util.Log;
import android.view.View;

import com.zero.support.work.Observable;

public abstract class DialogModel extends ActivityModel {
    private final Observable<DialogClickEvent> observable = new Observable<>();
    private Dialog dialog;
    private int which;
    private String dialogName;

    public boolean isEnableCached() {
        return false;
    }

    public String getDialogName() {
        return dialogName;
    }

    public DialogModel() {
    }

    public DialogModel(String dialogName) {
        this.dialogName = dialogName;
    }

    public Observable<DialogClickEvent> click() {
        return observable;
    }

    final void show(Dialog dialog) {
        which = 0;
        this.dialog = dialog;
        this.dialog.show();
        onBindDialog(dialog);
    }

    protected void onBindDialog(Dialog dialog) {

    }

    public boolean isClicked() {
        return which != 0;
    }


    public Dialog getDialog() {
        return dialog;
    }

    public final Dialog requireDialog() {
        final Dialog dialog = this.dialog;
        if (dialog == null) {
            throw new IllegalStateException("dialog is null");
        }
        return dialog;
    }

    void detachDialog(Dialog dialog) {
        if (this.dialog == dialog && dialog != null) {
            dialog.dismiss();
            this.dialog = null;
        }
    }

    public void dismiss() {
        SupportViewModel viewModel = requireViewModel();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (viewModel != null) {
            viewModel.removeDialog(this);
        }
        dialog = null;
    }

    public final void dispatchClickEvent(View view, int which) {
        if (dialog!=null){
            this.which = which;
            onClick(view, which);
            observable.setValue(new DialogClickEvent(this, which));
        }else {
            Log.e("dialog", "dispatchClickEvent: ignore, reason : dismiss" );
        }
    }


    public int which() {
        return which;
    }

    public void onClick(View view, int which) {

    }

    protected abstract Dialog onCreateDialog(SupportActivity activity);

}
