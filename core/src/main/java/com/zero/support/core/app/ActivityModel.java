package com.zero.support.core.app;

public class ActivityModel {
    private InjectViewModel injectViewModel;

    public final void attach(InjectViewModel injector) {
        this.injectViewModel = injector;
    }

    public final void detach() {
        this.injectViewModel = null;
    }

    public final InjectViewModel requireViewModel() {
        if (injectViewModel == null) {
            throw new RuntimeException("model is detach");
        }
        return injectViewModel;
    }

    public InjectViewModel getViewModel() {
        return injectViewModel;
    }

    public boolean isAttached() {
        return injectViewModel != null;
    }
}
