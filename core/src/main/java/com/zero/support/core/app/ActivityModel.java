package com.zero.support.core.app;

public class ActivityModel {
    private InjectViewModel injectViewModel;

    final void attach(InjectViewModel injector) {
        this.injectViewModel = injector;
        onAttachedViewModel(injector);
    }

    protected void onAttachedViewModel(InjectViewModel injector) {

    }

    final void detach() {
        onDetachedViewModel();
        this.injectViewModel = null;
    }

    protected void onDetachedViewModel() {

    }

    public final InjectViewModel requireViewModel() {
        if (injectViewModel == null) {
            throw new RuntimeException("model is detach");
        }
        return injectViewModel;
    }

    public final InjectViewModel getViewModel() {
        return injectViewModel;
    }

    public boolean isAttached() {
        return injectViewModel != null;
    }
}
