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
        return injectViewModel;
    }

    public boolean isAttached() {
        return injectViewModel != null;
    }
}
