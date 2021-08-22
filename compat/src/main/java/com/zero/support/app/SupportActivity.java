package com.zero.support.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.zero.support.compat.ActivityManager;

import java.util.HashMap;
import java.util.Map;


public class SupportActivity extends AppCompatActivity implements Injector {
    private final Map<Class<?>, SupportViewModel> viewModels = new HashMap<>();
    private InjectorViewModel injector;

    public String getActivityName() {
        return getClass().getSimpleName();
    }

    public final InjectorViewModel injectViewModel() {
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

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ActivityManager.createActivityWindow(getWindow().getDecorView().getWindowToken(), getActivityName());
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ActivityManager.destroyWindow(getWindow().getDecorView().getWindowToken());
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
        ActivityManager.destroyWindow(getWindow().getDecorView().getWindowToken());
        for (SupportViewModel viewModel : viewModels.values()) {
            viewModel.detachContext();
        }
        super.onDestroy();
    }

    @Override
    public Dialog onCreateDialog(DialogModel model, Activity activity) {
        return model.onCreateDialog(activity);
    }

    @Override
    public boolean dispatchMessage(Object msg) {
        return false;
    }
}
