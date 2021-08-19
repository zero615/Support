package com.zero.support.compat.app;

import android.app.Dialog;

import com.zero.support.work.AppExecutor;

public final class Tip extends DialogModel implements Runnable {
    public static final int TYPE_LOADING = 1;
    /**
     * 显示成功图标
     */
    public static final int TYPE_SUCCESS = 2;
    /**
     * 显示失败图标
     */
    public static final int TYPE_FAIL = 3;
    /**
     * 显示信息图标
     */
    public static final int TYPE_INFO = 4;
    public int type;
    public String message;

    public Tip(int type, String message) {
        this.type = type;
        this.message = message;
    }


    public void dismiss() {
        SupportViewModel viewModel = requireViewModel();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (viewModel != null) {
            viewModel.removeTip(this);
        }
        detachDialog(dialog);
    }

    @Override
    protected void onBindDialog(Dialog dialog) {
        TipDialog tipDialog = (TipDialog) dialog;
        tipDialog.dispatchTipEvent(this);
        if (type == TYPE_SUCCESS) {
            AppExecutor.getMainHandler().postDelayed(this, 700);
        } else if (type != TYPE_LOADING) {
            AppExecutor.getMainHandler().postDelayed(this, 1500);
        }
    }
    
    @Override
    protected Dialog onCreateDialog(SupportActivity activity) {
        return new TipDialog(activity);
    }

    @Override
    public void run() {
        dismiss();
    }
}
