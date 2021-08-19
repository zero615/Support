package com.zero.support.compat.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import java.util.HashMap;
import java.util.Map;

public class SupportFragment extends Fragment {
    private final Map<Class<?>, SupportViewModel> viewModels = new HashMap<>();

    public <T extends SupportViewModel> T attachSupportViewModel(Class<T> aClass) {
        @SuppressWarnings("ALL")
        T viewModel = (T) viewModels.get(aClass);
        if (viewModel == null) {
            viewModel = onCreateViewModel(aClass);
            viewModels.put(viewModel.getClass(), viewModel);
            viewModel.attachRequestViewModel(requireSupportActivity().peekViewModel(RequestViewModel.class), this);
        }
        return viewModel;
    }

    protected <T extends SupportViewModel> T onCreateViewModel(Class<T> aClass) {
        return new ViewModelProvider(this).get(aClass);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return super.getViewModelStore();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    public <T extends SupportViewModel> T peekViewModel(Class<T> aClass) {
        return (T) viewModels.get(aClass);
    }

    @NonNull
    @Override
    public LayoutInflater onGetLayoutInflater(@Nullable Bundle savedInstanceState) {
        return super.onGetLayoutInflater(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public SupportActivity requireSupportActivity() {
        Activity activity = requireActivity();
        if (activity instanceof SupportActivity) {
            return (SupportActivity) activity;
        }
        return null;
    }

    public SupportActivity getSupportActivity() {
        Activity activity = getActivity();
        if (activity instanceof SupportActivity) {
            return (SupportActivity) activity;
        }
        return null;
    }

    public RequestViewModel requireRequestViewModel() {
        return requireSupportActivity().requestViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (SupportViewModel viewModel : viewModels.values()) {
            viewModel.detachContext();
        }
    }


}
