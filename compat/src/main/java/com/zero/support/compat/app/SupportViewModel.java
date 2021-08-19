package com.zero.support.compat.app;

import android.app.Application;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.zero.support.compat.observable.SingleLiveEvent;
import com.zero.support.work.AppExecutor;


public abstract class SupportViewModel extends ContextViewModel {
    private final SingleLiveEvent<String> messageEvent = new SingleLiveEvent<>();


    private Object mValue;
    private RequestViewModel requestViewModel;

    public RequestViewModel requestViewModel() {
        return requestViewModel;
    }

    protected static void assertMainThread(String methodName) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new IllegalStateException("Cannot invoke " + methodName + " on a background"
                    + " thread");
        }
    }

    final void attachRequestViewModel(RequestViewModel viewModel, SupportFragment fragment) {
        if (this.requestViewModel == null) {
            this.requestViewModel = viewModel;
            onViewModelCreated(fragment);
        }
        attachFragment(fragment);
    }

    final void attachRequestViewModel(RequestViewModel viewModel, SupportActivity activity) {
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

    public LiveData<String> obtainMessageEvent() {
        return messageEvent;
    }


    public void tip(int type, String message) {
        requestTip(new Tip(type, message));
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

    public void postMessage(String message) {
        messageEvent.postValue(message);
    }

    public void postMessage(int id) {
        final Application application = getApplication();
        if (application != null) {
            postMessage(application.getString(id));
        }
    }

    public void postSuccess(int id) {
        final Application application = getApplication();
        if (application != null) {
            postSuccess(application.getString(id));
        }
    }

    public void postSuccess(String message) {
        postTip(Tip.TYPE_SUCCESS, message);
    }

    public void success(String message) {
        tip(Tip.TYPE_SUCCESS, message);
    }

    public void error(String message) {
        tip(Tip.TYPE_FAIL, message);
    }

    public void postError(int id) {
        final Application application = getApplication();
        if (application != null) {
            postError(application.getString(id));
        }
    }

    public void postError(String message) {
        postTip(Tip.TYPE_FAIL, message);
    }

    public void loading(String message) {
        tip(Tip.TYPE_LOADING, message);
    }

    public void postLoading(int id) {
        final Application application = getApplication();
        if (application != null) {
            postLoading(application.getString(id));
        }
    }

    public void postLoading(String message) {
        postTip(Tip.TYPE_LOADING, message);
    }

    public void loading() {
        tip(Tip.TYPE_LOADING, null);
    }

    public void dismiss() {
        assertMainThread("dismiss");
        Tip tip = requestViewModel.getCurrentTip();
        if (tip != null) {
            tip.dismiss();
        }
    }

    public void postDismiss() {
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


    public PermissionModel requestPermission(PermissionModel model) {
        return requestViewModel.performRequestPermission(model);
    }

    public Tip requestTip(Tip tip) {
        requestViewModel.performRequestTips(tip);
        return tip;
    }

    public DialogModel requestDialog(DialogModel model) {
        return requestViewModel.performRequestDialog(model);
    }

    public ActivityResultModel requestActivityResult(ActivityResultModel model) {
        return requestViewModel.performRequestActivityResult(model);
    }

    public void removeDialog(DialogModel model) {
        requestViewModel.removeDialogModel(model);
    }

    public void removeTip(Tip tip) {
        requestViewModel.removeTipModel(tip);
    }
}
