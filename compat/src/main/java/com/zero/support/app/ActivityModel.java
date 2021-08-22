package com.zero.support.app;

public class ActivityModel {
    private InjectorViewModel injectorViewModel;

    public final void attach(InjectorViewModel injector) {
        this.injectorViewModel = injector;
    }

    public final void detach() {
        this.injectorViewModel = null;
    }

    public final InjectorViewModel requireViewModel() {
        return injectorViewModel;
    }

    public boolean isAttached() {
        return injectorViewModel != null;
    }
}
