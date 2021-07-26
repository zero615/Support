package com.zero.support.compat.app;

import android.app.Activity;
import android.app.Application;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.zero.support.compat.observable.SingleLiveEvent;


public abstract class SupportViewModel extends ContextViewModel {
    private final SingleLiveEvent<String> messageEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Tip> tipEvent = new SingleLiveEvent<>();

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

    final void attachRequestViewModel(RequestViewModel viewModel) {
        if (this.requestViewModel == null) {
            this.requestViewModel = viewModel;
            onViewModelCreated();
        }
    }

    protected void onViewModelCreated() {

    }

    @Override
    void attach(SupportActivity activity) {
        super.attach(activity);
        if (requestViewModel == null) {
            requestViewModel = activity.peekViewModel(RequestViewModel.class);
        }
        onAttach(activity);
    }

    protected void onAttach(Activity activity) {

    }

    protected void onDetach() {

    }

    @Override
    void attach(SupportFragment fragment) {
        super.attach(fragment);
        if (requestViewModel == null) {
            requestViewModel = fragment.peekViewModel(RequestViewModel.class);
        }
        onAttach(fragment.requireActivity());
    }

    @Override
    void detach() {
        super.detach();
        onDetach();
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

    public LiveData<Tip> obtainTipEvent() {
        return tipEvent;
    }


    public void tip(int type, String message) {
        tipEvent.setValue(new Tip(type, message));
    }

    public void postTip(int type, String message) {
        tipEvent.postValue(new Tip(type, message));
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
        tipEvent.setValue(new Tip(Tip.TYPE_DISMISS, null));

    }

    public void postDismiss() {
        postTip(Tip.TYPE_DISMISS, null);
    }


    public PermissionModel requestPermission(PermissionModel model) {
        return requestViewModel.performRequestPermission(model);
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
}
