package com.zero.support.core.app;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.zero.support.core.AppGlobal;
import com.zero.support.core.observable.Observable;
import com.zero.support.core.observable.PromiseObservable;
import com.zero.support.core.observable.SingleLiveEvent;

import java.util.HashMap;
import java.util.Map;

public class InjectorHelper {
    final Map<Class<?>, Object> targets = new HashMap<>();
    final Map<LayerModel<?>, Object> windowTargets = new HashMap<>();

    final PromiseObservable<PermissionModel> permissionModels = new PromiseObservable<>();

    final PromiseObservable<ActivityResultModel> resultModels = new PromiseObservable<>();

    final PromiseObservable<LayerModel<?>> serialWindows = new PromiseObservable<>();

    final PromiseObservable<LayerModel<?>> notificationWindows = new PromiseObservable<>();

    final Observable<LayerModel<?>> windows = new PromiseObservable<>();

    final SingleLiveEvent<Object> messageEvent = new SingleLiveEvent<>();

    private InjectFragment fragment;

    public void onAttachFragment(final InjectViewModel viewModel, final InjectFragment fragment) {
        this.fragment = fragment;
        permissionModels.asLive().observe(fragment, new Observer<PermissionModel>() {
            @Override
            public void onChanged(PermissionModel model) {
                if (model == null) {
                    return;
                }
                if (!model.isExecuted()) {
                    model.setExecuted(true);
                    PermissionHelper.requestPermission(viewModel, 100, model.permissions());
                }
            }
        });
        resultModels.asLive().observe(fragment, new Observer<ActivityResultModel>() {
            @Override
            public void onChanged(ActivityResultModel model) {
                if (model == null) {
                    return;
                }
                if (!model.isExecuted()) {
                    model.setExecuted(true);
                    fragment.startActivityForResult(model.intent(), 100);
                }
            }
        });
        Observer<LayerModel<?>> observer = new Observer<LayerModel<?>>() {
            @Override
            public void onChanged(LayerModel<?> model) {
                if (model == null) {
                    return;
                }
                dispatchDialogEvent(model);
            }
        };
        serialWindows.asLive().observe(fragment, observer);
        notificationWindows.asLive().observe(fragment, observer);
        messageEvent.observe(fragment, new Observer<Object>() {
            @Override
            public void onChanged(@Nullable Object msg) {
                Activity activity = viewModel.requireActivity();
                boolean handled = false;
                Injector injector = fragment.getInjectViewModel().getCurrentInjector();
                if (injector != null && activity != injector) {
                    handled = injector.dispatchMessage(msg);
                }
                if (activity instanceof InjectViewModel.MessageDispatcher) {
                    handled = ((InjectViewModel.MessageDispatcher) activity).dispatchMessage(msg);
                }
                if (!handled) {
                    AppGlobal.sendMessage(msg);
                }
            }
        });
    }


    @SuppressWarnings("all")
    protected void dispatchDialogEvent(LayerModel model) {
        Object target = targets.get(model.getClass());
        if (target == null) {
            Activity activity = fragment.getActivity();
            Injector injector = fragment.getInjectViewModel().getCurrentInjector();
            if (injector != null && activity != injector) {
                target = injector.onCreateTarget(model, activity);
            }
            if (activity instanceof InjectViewModel.WindowCreator) {
                target = ((InjectViewModel.WindowCreator) activity).onCreateTarget(model, activity);
            }
            if (target == null) {
                target = model.onCreateLayer(fragment.getActivity());
                if (target == null) {
                    Log.e("support", "dispatchDialogEvent: fail for " + model);
                    model.dismiss();
                    return;
                }
            }
            targets.put(model.getClass(), target);
        }
        windowTargets.put(model, target);
        model.show(target);
    }


    final void dispatchRequestPermission(String[] permissions, int[] grantResults) {
        PermissionModel model = permissionModels.getValue();
        if (model != null) {
            model.dispatchResult(permissions, grantResults);
            model.detach();
            permissionModels.remove(model);
        }
    }

    final void dispatchRequestResult(int resultCode, Intent data) {
        ActivityResultModel model = resultModels.getValue();
        if (model != null) {
            model.dispatchResult(resultCode, data);
            model.detach();
            resultModels.remove(model);
        }
    }


    @SuppressWarnings("all")
    final void detachFragment() {
        for (LayerModel dialog : windowTargets.keySet()) {
            dialog.detachLayer();
        }
        windowTargets.clear();
        targets.clear();
    }

    final void requestWindowAtFront(LayerModel<?> model) {
        if (model.isAttached()) {
            model.dismiss();
        }
        int windowType = model.getLayerType();
        if (windowType == LayerModel.TYPE_SERIAL) {
            model.attach(fragment.getInjectViewModel());
            serialWindows.postAtFront(model);
        } else if (windowType == LayerModel.TYPE_SERIAL_NOTIFICATION) {
            LayerModel<?> layerModel = notificationWindows.getValue();
            if (layerModel != null) {
                layerModel.dismiss();
            }
            model.attach(fragment.getInjectViewModel());
            notificationWindows.postAtFront(model);
        } else if (windowType == LayerModel.TYPE_ALL) {
            model.attach(fragment.getInjectViewModel());
            windows.setValue(model);
        }
    }

    final void requestWindow(LayerModel<?> model) {
        if (model.isAttached()) {
            model.dismiss();
        }
        int windowType = model.getLayerType();
        if (windowType == LayerModel.TYPE_SERIAL) {
            model.attach(fragment.getInjectViewModel());
            serialWindows.setValue(model);
        } else if (windowType == LayerModel.TYPE_SERIAL_NOTIFICATION) {
            LayerModel<?> layerModel = notificationWindows.getValue();
            if (layerModel != null) {
                layerModel.dismiss();
            }
            model.attach(fragment.getInjectViewModel());
            notificationWindows.setValue(model);
        } else if (windowType == LayerModel.TYPE_ALL) {
            model.attach(fragment.getInjectViewModel());
            windows.setValue(model);
        }
    }

    final LayerModel<?> getCurrentWindowModel(int windowType) {
        if (windowType == LayerModel.TYPE_SERIAL) {
            return serialWindows.getValue();
        } else if (windowType == LayerModel.TYPE_SERIAL_NOTIFICATION) {
            return notificationWindows.getValue();
        } else if (windowType == LayerModel.TYPE_ALL) {
            return windows.getValue();
        }
        return null;
    }

    @SuppressWarnings("all")
    final void removeWindow(LayerModel model) {
        Object target = windowTargets.remove(model);
        if (!model.isEnableCached()) {
            targets.remove(model.getClass());
        }
        if (target != null) {
            model.detachLayer();
        }
        model.detach();
        int windowType = model.getLayerType();
        if (windowType == LayerModel.TYPE_SERIAL) {
            serialWindows.remove(model);
        } else if (windowType == LayerModel.TYPE_SERIAL_NOTIFICATION) {
            notificationWindows.remove(model);
        } else if (windowType == LayerModel.TYPE_ALL) {
            windows.reset();
        }
    }

}
