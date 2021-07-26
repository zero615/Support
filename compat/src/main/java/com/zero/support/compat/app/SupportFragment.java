package com.zero.support.compat.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
            viewModel.attach(this);
            attachSupportViewModel(viewModel);
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
        final RequestViewModel viewModel = attachSupportViewModel(RequestViewModel.class);
        viewModel.obtainPermission().observe(this, new Observer<PermissionModel>() {
            @Override
            public void onChanged(PermissionModel model) {
                if (model == null) {
                    return;
                }
                PermissionHelper.requestPermission(viewModel, 100, model.permissions());
            }
        });
        viewModel.obtainActivityResult().observe(this, new Observer<ActivityResultModel>() {
            @Override
            public void onChanged(ActivityResultModel model) {
                if (model == null) {
                    return;
                }
                startActivityForResult(model.intent(), 100);
            }
        });
        viewModel.obtainDialog().observe(this, new Observer<DialogModel>() {
            @Override
            public void onChanged(DialogModel model) {
                if (model == null) {
                    return;
                }
                dispatchDialogEvent(model);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            RequestViewModel viewModel = peekViewModel(RequestViewModel.class);
            if (viewModel != null) {
                viewModel.dispatchRequestPermission(permissions, grantResults);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            RequestViewModel viewModel = peekViewModel(RequestViewModel.class);
            if (viewModel != null) {
                viewModel.dispatchRequestResult(resultCode, data);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends SupportViewModel> T peekViewModel(Class<T> aClass) {
        return (T) viewModels.get(aClass);
    }

    private void attachSupportViewModel(SupportViewModel viewModel) {
        viewModel.obtainMessageEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    dispatchMessageEvent(s);
                }
            }
        });
        viewModel.obtainTipEvent().observe(this, new Observer<Tip>() {
            @Override
            public void onChanged(@Nullable Tip tip) {
                if (tip == null) {
                    return;
                }

            }
        });
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

    protected void dispatchMessageEvent(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message + "", Toast.LENGTH_SHORT).show();
        }

    }

    protected void dispatchTipEvent(Tip tip) {
        Activity activity = requireActivity();
        if (activity instanceof SupportActivity) {
            ((SupportActivity) activity).dispatchTipEvent(tip);
        }
    }

    protected void dispatchDialogEvent(DialogModel remoteDialogModel) {
        Activity activity = requireActivity();
        if (activity instanceof SupportActivity) {
            ((SupportActivity) activity).dispatchDialogEvent(remoteDialogModel);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (SupportViewModel viewModel : viewModels.values()) {
            viewModel.detach();
        }
    }


}
