package com.zero.support.xx.compat;

public class ActivityModel {
    private InjectViewModel viewModel;

    public final void attach(InjectViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public final void detach() {
        this.viewModel = null;
    }

    public final InjectViewModel requireViewModel() {
        return viewModel;
    }

    public boolean isAttached() {
        return viewModel != null;
    }
}
