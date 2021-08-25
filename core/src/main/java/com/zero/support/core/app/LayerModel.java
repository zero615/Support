package com.zero.support.core.app;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LayerModel<T> extends ActivityModel {
    private T layer;
    private boolean dismiss;

    /**
     * 该类型window，按顺序弹出
     */
    public static final int TYPE_SERIAL = 1;
    /**
     * 该类型window，按顺序弹出，弹出时间较短
     */
    public static final int TYPE_SERIAL_NOTIFICATION = 2;
    /**
     * 该类型window，直接弹出,类似悬浮球应用场景
     */
    public static final int TYPE_ALL = 3;


    private final int layerType;

    public LayerModel(int layerType) {
        this.layerType = layerType;
    }

    final void show(T layer) {
        dismiss = false;
        this.layer = layer;
        onAttachedToModel(layer);
    }

    final int getLayerType() {
        return layerType;
    }

    protected boolean isEnableCached() {
        return false;
    }


    /**
     * 视图绑定到WindowModel时调用
     */
    protected void onAttachedToModel(T layer) {

    }


    /**
     * 视图解绑WindowModel时调用
     */
    protected void onDetachedFromModel(T layer) {

    }

    /**
     * 创建一个视图
     */
    protected T onCreateLayer(Activity activity) {
        return null;
    }

    final void detachLayer() {
        onDetachedFromModel(layer);
        layer = null;
    }


    @Nullable
    public final T getLayer() {
        return layer;
    }

    @NonNull
    public final T requireLayer() {
        if (layer == null) {
            throw new RuntimeException("not attach target");
        }
        return layer;
    }

    public final void dismiss() {
        if (!dismiss) {
            dismiss = true;
            requireViewModel().removeWindow(this);
        }
    }

    public boolean isDismiss() {
        return dismiss;
    }
}
