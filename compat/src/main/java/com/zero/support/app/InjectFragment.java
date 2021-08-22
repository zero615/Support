package com.zero.support.app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class InjectFragment extends Fragment implements LifecycleOwner {
    private InjectorViewModel injectViewModel;
    private final LifecycleRegistry registry = new LifecycleRegistry(this);

    public InjectorViewModel getInjectViewModel() {
        return injectViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        injectViewModel = new InjectorViewModel();
        registry.setCurrentState(Lifecycle.State.INITIALIZED);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registry.setCurrentState(Lifecycle.State.CREATED);
        injectViewModel.attachFragment(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        injectViewModel.detachFragment();
        registry.setCurrentState(Lifecycle.State.DESTROYED);
    }

    @Override
    public void onStart() {
        super.onStart();
        registry.setCurrentState(Lifecycle.State.STARTED);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        registry.setCurrentState(Lifecycle.State.RESUMED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (injectViewModel != null) {
                injectViewModel.getInjectorHelper().dispatchRequestPermission(permissions, grantResults);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            if (injectViewModel != null) {
                injectViewModel.getInjectorHelper().dispatchRequestResult(resultCode, data);
            }
        }
    }

    @NonNull

    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }
}
