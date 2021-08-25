package com.zero.support.core.app;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;

import com.zero.support.core.observable.Observable;


public abstract class DialogModel extends LayerModel<Dialog> {
    private final Observable<DialogClickEvent> observable = new Observable<>();
    private int which;

    public DialogModel() {
        super(LayerModel.TYPE_SERIAL);
    }

    public DialogModel(int windowType) {
        super(windowType);
    }

    public final Observable<DialogClickEvent> click() {
        return observable;
    }


    @Override
    protected void onAttachedToModel(Dialog layer) {
        super.onAttachedToModel(layer);
        which = 0;
        this.click().reset();
        if (!layer.isShowing()) {
            layer.show();
        }
    }

    @Override
    protected Dialog onCreateLayer(Activity activity) {
        return super.onCreateLayer(activity);
    }

    @Override
    protected void onDetachedFromModel(Dialog layer) {
        super.onDetachedFromModel(layer);
        if (layer.isShowing()) {
            layer.dismiss();
        }
    }

    public boolean isClicked() {
        return which != 0;
    }


    public final void dispatchClickEvent(View view, int which) {
        if (this.getLayer() != null) {
            this.which = which;
            observable.setValue(new DialogClickEvent(this, which));
            onClick(view, which);
        } else {
            Log.e("model", "dispatchClickEvent: dialog is dismiss");
        }
    }


    public final int which() {
        return which;
    }

    protected void onClick(View view, int which) {
    }
}
