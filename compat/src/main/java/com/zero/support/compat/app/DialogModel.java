package com.zero.support.compat.app;

import android.app.Dialog;
import android.view.View;

import com.zero.support.work.Observable;

public abstract class DialogModel extends ActivityModel {
    private final Observable<DialogClickEvent> observable = new Observable<>();
    private Dialog dialog;
    private boolean dismiss;
    private boolean clicked;
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


    public final void show(Dialog dialog) {
        this.dialog = dialog;
        if (clicked) {
            clicked = false;
            dismiss = false;
            which = 0;
        }
        if (!dismiss) {
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }


    public boolean isClicked() {
        return which != 0;
    }


    public final Dialog requireDialog() {
        return dialog;
    }

    public void detachDialog(Dialog dialog) {
        if (this.dialog == dialog) {
            dialog.dismiss();
            this.dialog = null;
        }
    }

    public void dismiss() {
        dismiss = true;
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
        this.clicked = true;
        this.which = which;
        onClick(view, which);
        observable.setValue(new DialogClickEvent(this, which));
    }


    public int which() {
        return which;
    }

    public void onClick(View view, int which) {

    }

    protected abstract Dialog onCreateDialog(SupportActivity activity);

}
