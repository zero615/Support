package com.zero.support.core.app;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;

import com.zero.support.core.observable.Observable;


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
        this.click().reset();
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
        InjectViewModel viewModel = requireViewModel();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (viewModel != null) {
            viewModel.removeDialog(this);
        }
        dialog = null;
    }

    public final void dispatchClickEvent(View view, int which) {
        if (this.dialog != null) {
            this.which = which;
            observable.setValue(new DialogClickEvent(this, which));
            onClick(view, which);
        } else {
            Log.e("xgf", "dispatchClickEvent: dialog is dismiss");
        }
    }


    public int which() {
        return which;
    }

    protected void onClick(View view, int which) {
        Log.e("xgf", "onClick: " + which + "  " + this);
    }

    protected abstract Dialog onCreateDialog(Activity activity);

}
