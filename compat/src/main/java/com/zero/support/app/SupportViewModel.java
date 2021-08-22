package com.zero.support.app;

import android.os.Looper;

import androidx.annotation.StringRes;

import com.zero.support.work.AppExecutor;


public abstract class SupportViewModel extends ContextViewModel {
    private Object mValue;
    private InjectorViewModel requestViewModel;

    public InjectorViewModel requestViewModel() {
        return requestViewModel;
    }

    protected static void assertMainThread(String methodName) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Cannot invoke " + methodName + " on a background"
                    + " thread");
        }
    }

    final void attachInjectViewModel(InjectorViewModel viewModel, SupportFragment fragment) {
        if (this.requestViewModel == null) {
            this.requestViewModel = viewModel;
            onViewModelCreated(fragment);
        }
        attachFragment(fragment);
    }

    final void attachInjectViewModel(InjectorViewModel viewModel, SupportActivity activity) {
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
            requestTip(tip);
        } else {
            AppExecutor.main().execute(new Runnable() {
                @Override
                public void run() {
                    requestTip(tip);
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
        Tip tip = requestViewModel.getCurrentTip();
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

    public final void requestTip(Tip tip) {
        requestViewModel.requestTip(tip);
    }

    public final DialogModel requestDialog(DialogModel model) {
        return requestViewModel.requestDialog(model);
    }

    public final ActivityResultModel requestActivityResult(ActivityResultModel model) {
        return requestViewModel.requestActivityResult(model);
    }
}
