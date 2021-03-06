package com.zero.support.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.zero.support.core.app.ActivityInjector;
import com.zero.support.core.app.InjectViewModel;
import com.zero.support.core.app.Injector;
import com.zero.support.core.app.LayerModel;

import java.util.HashMap;
import java.util.Map;


public class SupportActivity extends AppCompatActivity implements Injector {
    private final Map<Class<?>, SupportViewModel> viewModels = new HashMap<>();
    private InjectViewModel injector;

    public String getActivityName() {
        return getClass().getSimpleName();
    }

    public final InjectViewModel injectViewModel() {
        return injector;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector = ActivityInjector.requireInjectViewModel(this);
    }

    public final <T extends SupportViewModel> T attachSupportViewModel(Class<T> aClass) {
        @SuppressWarnings("ALL")
        T viewModel = (T) viewModels.get(aClass);
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this).get(aClass);
            viewModels.put(viewModel.getClass(), viewModel);
            viewModel.attachInjectViewModel(injector, this);
        }
        return viewModel;
    }

    @SuppressWarnings("all")
    public final <T extends SupportViewModel> T peekViewModel(Class<T> aClass) {
        return (T) viewModels.get(aClass);
    }


    @Override
    protected void onDestroy() {
        for (SupportViewModel viewModel : viewModels.values()) {
            viewModel.detachContext();
        }
        super.onDestroy();
    }

    @Override
    public boolean dispatchMessage(Object msg) {
        return false;
    }

    @Override
    public Object onCreateTarget(LayerModel<?> model, Activity activity) {
        return null;
    }
}
