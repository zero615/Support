package com.zero.support.compat.app;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModel;

class ObservableViewModel extends ViewModel implements Observable, LifecycleOwner {
    @SuppressLint("StaticFieldLeak")
    private final LifecycleRegistry registry;
    private transient PropertyChangeRegistry mCallbacks;

    public ObservableViewModel() {
        registry = new LifecycleRegistry(this);
        registry.setCurrentState(Lifecycle.State.CREATED);
        registry.setCurrentState(Lifecycle.State.STARTED);
        registry.setCurrentState(Lifecycle.State.RESUMED);
    }

    @Override
    public void addOnPropertyChangedCallback(@NonNull OnPropertyChangedCallback callback) {
        synchronized (this) {
            if (mCallbacks == null) {
                mCallbacks = new PropertyChangeRegistry();
            }
        }
        mCallbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(@NonNull OnPropertyChangedCallback callback) {
        synchronized (this) {
            if (mCallbacks == null) {
                return;
            }
        }
        mCallbacks.remove(callback);
    }


    public void notifyChange() {
        synchronized (this) {
            if (mCallbacks == null) {
                return;
            }
        }
        mCallbacks.notifyCallbacks(this, 0, null);
    }


    public void notifyPropertyChanged(int fieldId) {
        synchronized (this) {
            if (mCallbacks == null) {
                return;
            }
        }
        mCallbacks.notifyCallbacks(this, fieldId, null);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        registry.setCurrentState(Lifecycle.State.DESTROYED);
    }
}
