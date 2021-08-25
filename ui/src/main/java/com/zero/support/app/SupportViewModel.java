package com.zero.support.app;

import android.os.Looper;

import androidx.annotation.StringRes;

import com.zero.support.core.AppExecutor;
import com.zero.support.core.app.ActivityResultModel;
import com.zero.support.core.app.InjectViewModel;
import com.zero.support.core.app.PermissionModel;
import com.zero.support.core.app.Tip;
import com.zero.support.core.app.LayerModel;


public abstract class SupportViewModel extends ContextViewModel {
    private Object mValue;
    private InjectViewModel requestViewModel;

    public InjectViewModel requestViewModel() {
        return requestViewModel;
    }

    protected static void assertMainThread(String methodName) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Cannot invoke " + methodName + " on a background"
                    + " thread");
        }
    }

    final void attachInjectViewModel(InjectViewModel viewModel, SupportFragment fragment) {
        if (this.requestViewModel == null) {
            this.requestViewModel = viewModel;
            onViewModelCreated(fragment);
        }
        attachFragment(fragment);
    }

    final void attachInjectViewModel(InjectViewModel viewModel, SupportActivity activity) {
        if (this.requestViewModel == null) {
            this.requestViewModel = viewModel;
            onViewModelCreated(activity);
        }
        attachActivity(activity);
    }

    protected void onViewModelCreated(SupportActivity activity) {

    }

    protected void onViewModelCreated(SupportFragment fragment) {

    }

    public <T> T getValue() {
        return (T) mValue;
    }

    public void setValue(Object mValue) {
        this.mValue = mValue;
    }

    public void postTip(int type, String message) {
        final Tip tip = new Tip(type, message);
        if (AppExecutor.isMainThread()) {
            requestWindowModel(tip);
        } else {
            AppExecutor.main().execute(new Runnable() {
                @Override
                public void run() {
                    requestWindowModel(tip);
                }
            });
        }
    }

    public void postMessage(Object message) {
        requestViewModel.postMessage(message);
    }


    public final String getString(@StringRes int id, Object... format) {
        return getApplication().getString(id, format);
    }

    public final void dismiss() {
        assertMainThread("dismiss");
        LayerModel tip = requestViewModel.getCurrentWindowModel(LayerModel.TYPE_SERIAL_NOTIFICATION);
        if (tip != null) {
            tip.dismiss();
        }
    }

    public final void postDismiss() {
        if (AppExecutor.isMainThread()) {
            dismiss();
        } else {
            AppExecutor.main().execute(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
        }
    }

    public final PermissionModel requestPermission(PermissionModel model) {
        return requestViewModel.requestPermission(model);
    }

    public final <T extends LayerModel<?>> T requestWindowModel(T model) {
        return requestViewModel.requestWindow(model);
    }

    public final <T extends LayerModel<?>> T requestWindowAtFront(T model) {
        return requestViewModel.requestWindow(model);
    }

    public final ActivityResultModel requestActivityResult(ActivityResultModel model) {
        return requestViewModel.requestActivityResult(model);
    }
}
