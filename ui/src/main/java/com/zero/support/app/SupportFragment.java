package com.zero.support.app;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.zero.support.core.app.ActivityInjector;
import com.zero.support.core.app.DialogModel;
import com.zero.support.core.app.InjectViewModel;
import com.zero.support.core.app.Injector;

import java.util.HashMap;
import java.util.Map;

public class SupportFragment extends Fragment implements Injector {
    private final Map<Class<?>, SupportViewModel> viewModels = new HashMap<>();
    private InjectViewModel injectViewModel;
    private Injector lastInjector;

    public final <T extends SupportViewModel> T attachSupportViewModel(Class<T> aClass) {
        @SuppressWarnings("ALL")
        T viewModel = (T) viewModels.get(aClass);
        if (viewModel == null) {
            viewModel = onCreateViewModel(aClass);
            viewModels.put(viewModel.getClass(), viewModel);
            viewModel.attachInjectViewModel(injectViewModel, this);
        }
        return viewModel;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (hidden) {
//            injectViewModel.setCurrentInjector(lastInjector);
//        } else {
//            lastInjector = injectViewModel.getCurrentInjector();
//            injectViewModel.setCurrentInjector(this);
//        }
    }


    protected <T extends SupportViewModel> T onCreateViewModel(Class<T> aClass) {
        return new ViewModelProvider(this).get(aClass);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectViewModel = ActivityInjector.requireInjectViewModel(requireActivity());
    }

    @SuppressWarnings("unchecked")
    public <T extends SupportViewModel> T peekViewModel(Class<T> aClass) {
        return (T) viewModels.get(aClass);
    }

    public final SupportActivity requireSupportActivity() {
        Activity activity = requireActivity();
        if (activity instanceof SupportActivity) {
            return (SupportActivity) activity;
        }
        throw new RuntimeException("attach activity is not support");
    }

    public final SupportActivity getSupportActivity() {
        Activity activity = getActivity();
        if (activity instanceof SupportActivity) {
            return (SupportActivity) activity;
        }
        return null;
    }

    public final InjectViewModel injectViewModel() {
        return injectViewModel;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (SupportViewModel viewModel : viewModels.values()) {
            viewModel.detachContext();
        }
    }


    @Override
    public Dialog onCreateDialog(DialogModel model, Activity activity) {
        return null;
    }

    @Override
    public boolean dispatchMessage(Object msg) {
        return false;
    }
}
