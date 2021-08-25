package com.zero.support.core.app;

import android.app.Activity;
import android.app.Dialog;

import com.zero.support.core.AppExecutor;


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

    public static final int TYPE_DISMISS = 4;
    public int type;
    public String message;

    public Tip(int type, String message) {
        super(LayerModel.TYPE_SERIAL_NOTIFICATION);
        this.type = type;
        this.message = message;
    }

    public static Tip loading() {
        return new Tip(TYPE_LOADING, null);
    }

    @Override
    protected boolean isEnableCached() {
        return true;
    }

    @Override
    protected void onAttachedToModel(Dialog layer) {
        super.onAttachedToModel(layer);
        TipDialog tipDialog = (TipDialog) layer;
        tipDialog.dispatchTipEvent(this);
        if (type == TYPE_SUCCESS) {
            AppExecutor.getMainHandler().postDelayed(this, 700);
        } else if (type == TYPE_DISMISS) {
            dismiss();
        } else if (type != TYPE_LOADING) {
            AppExecutor.getMainHandler().postDelayed(this, 1500);
        }
    }

    @Override
    protected Dialog onCreateLayer(Activity activity) {
        return new TipDialog(activity);
    }

    @Override
    public void run() {
        dismiss();
    }
}
