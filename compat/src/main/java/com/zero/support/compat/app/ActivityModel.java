package com.zero.support.compat.app;

public class ActivityModel {
    private SupportViewModel viewModel;

    public final void attach(SupportViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public final void detach() {
        this.viewModel = null;
    }

    public final SupportViewModel requireViewModel() {
        return viewModel;
    }

    public boolean isAttached() {
        return viewModel != null;
    }
}
