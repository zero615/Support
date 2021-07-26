package com.zero.support.compat.app;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.zero.support.compat.ActivityManager;

import java.util.HashMap;
import java.util.Map;


public class SupportActivity extends AppCompatActivity {

    private final Map<Class<?>, Dialog> dialogs = new HashMap<>();
    private final Map<Class<?>, SupportViewModel> viewModels = new HashMap<>();
    private TipDialog dialog;
    private int containerId = android.R.id.content;

    public void bindFragmentContainer(int containerId) {
        this.containerId = containerId;
    }

    private TipDialog obtainTipDialog() {
        if (dialog == null) {
            dialog = new TipDialog(this);
        }
        return dialog;
    }

    public String getActivityName() {
        return getClass().getSimpleName();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RequestViewModel viewModel = attachSupportViewModel(RequestViewModel.class);
        viewModel.obtainPermission().observe(this, new Observer<PermissionModel>() {
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
        viewModel.obtainActivityResult().observe(this, new Observer<ActivityResultModel>() {
            @Override
            public void onChanged(ActivityResultModel model) {
                if (model == null) {
                    return;
                }
                if (!model.isExecuted()) {
                    model.setExecuted(true);
                    startActivityForResult(model.intent(), 100);
                }
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            RequestViewModel viewModel = peekViewModel(RequestViewModel.class);
            if (viewModel != null) {
                viewModel.dispatchRequestResult(resultCode, data);
            }
        }
    }


    public <T extends SupportViewModel> T attachSupportViewModel(Class<T> aClass) {
        @SuppressWarnings("ALL")
        T viewModel = (T) viewModels.get(aClass);
        if (viewModel == null) {
            viewModel = new ViewModelProvider(this).get(aClass);
            viewModels.put(viewModel.getClass(), viewModel);
            viewModel.attachRequestViewModel(peekViewModel(RequestViewModel.class));
            viewModel.attach(this);
            attachSupportViewModel(viewModel);
        }
        return viewModel;
    }

    public <T extends SupportViewModel> T peekViewModel(Class<T> aClass) {
        return (T) viewModels.get(aClass);
    }


    public void show(Class<? extends DialogFragment> aClass, Class<? extends SupportViewModel> viewModelClass, Object value) {
        DialogFragment fragment = null;
        FragmentManager manager = getSupportFragmentManager();
        fragment = (DialogFragment) manager.findFragmentByTag(aClass.getName());
        if (fragment == null) {
            try {
                fragment = aClass.newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            manager.beginTransaction().add(fragment, aClass.getName()).commitAllowingStateLoss();
            manager.executePendingTransactions();
        }

        if (viewModelClass != null) {
            new ViewModelProvider(this).get(viewModelClass).setValue(value);
        }
    }

    public void show(Class<? extends DialogFragment> aClass) {
        show(aClass, null, null);
    }

    public void inject(@IdRes int layoutId, Class<? extends Fragment> aClass) {
        inject(layoutId, aClass, null, null);
    }

    public void inject(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commitAllowingStateLoss();
    }

    public void inject(int layoutId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(layoutId, fragment).commitAllowingStateLoss();
    }

    public void replace(Class<? extends Fragment> fragment) {
        replace(containerId, fragment, Bundle.EMPTY);
    }

    public void replace(Class<? extends Fragment> fragment, Bundle extra) {
        replace(containerId, fragment, extra);
    }

    private void replace(int layoutId, Class<? extends Fragment> fragment, Bundle extra) {
        getSupportFragmentManager().beginTransaction().replace(layoutId, fragment, extra).commitNowAllowingStateLoss();
    }

    public void inject(@IdRes int layoutId, Class<? extends Fragment> aClass, Class<? extends SupportViewModel> viewModelClass, Object value) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(aClass.getName());
        if (fragment == null) {
            try {
                fragment = aClass.newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            manager.beginTransaction()
                    .add(layoutId, fragment, aClass.getName())
                    .commitAllowingStateLoss();
            // Hopefully, we are the first to make a transaction.
            manager.executePendingTransactions();
        }
        if (viewModelClass != null) {
            new ViewModelProvider(fragment).get(viewModelClass).setValue(value);
        }
    }


    private void attachSupportViewModel(final SupportViewModel viewModel) {
        viewModel.obtainMessageEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                dispatchMessageEvent(s);
            }
        });
        viewModel.obtainTipEvent().observe(this, new Observer<Tip>() {
            @Override
            public void onChanged(@Nullable Tip tip) {
                if (tip == null) {
                    return;
                }
                dispatchTipEvent(tip);
            }
        });


    }

    protected void dispatchTipEvent(Tip tip) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            obtainTipDialog().create();
        }
        obtainTipDialog().dispatchTipEvent(tip);
    }


    protected void dispatchMessageEvent(String message) {
        Toast.makeText(this, message + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        ActivityManager.destroyWindow(getWindow().getDecorView().getWindowToken());
        for (SupportViewModel viewModel : viewModels.values()) {
            viewModel.detach();
        }
        for (Dialog dialog : dialogs.values()) {
            if (dialog instanceof CommonDialog) {
                ((CommonDialog) dialog).setCurrentDialogModel(null);
            } else {
                dialog.dismiss();
            }
        }
        super.onDestroy();
    }

    void requestRemoveDialog(DialogModel model) {
        if (!model.isEnableCached()){
            dialogs.remove(model.getClass());
        }
    }

    protected void dispatchDialogEvent(DialogModel model) {
        Dialog dialog = dialogs.get(model.getClass());

        if (dialog == null) {
            dialog = model.onCreateDialog(this);
            if (dialog == null) {
                dialog = onCreateSupportDialog(model);
            }
            if (dialog == null) {
                Log.e("support", "dispatchDialogEvent: fail for " + model);
                return;
            }
            dialogs.put(model.getClass(), dialog);
        }
        model.show(dialog);
    }

    protected CommonDialog onCreateSupportDialog(DialogModel model) {
        return null;
    }

}
