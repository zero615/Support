package com.zero.support.work.util;


import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.zero.support.work.AppExecutor;
import com.zero.support.work.Observable;
import com.zero.support.work.Observer;

import java.util.Map;
import java.util.WeakHashMap;


@SuppressWarnings("ALL")
public class ObservableLiveData<T> extends MutableLiveData<T> {
    private final Observable<T> observable;

    private final Observer observer = new Observer<T>() {
        @Override
        public void onChanged(T t) {
            if (AppExecutor.isMainThread()) {
                setValue(t);
            } else {
                postValue(t);
            }
        }
    };

    private final Object NOT_SET;

    public ObservableLiveData(Observable<T> observable, Object notSet) {
        this.observable = observable;
        this.observable.observe(observer);
        NOT_SET = notSet;
    }

    @Override
    public void observeForever(@NonNull androidx.lifecycle.Observer<? super T> observer) {
        super.observeForever(new NotSetObserver(observer));
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull androidx.lifecycle.Observer<? super T> observer) {
        super.observe(owner, new NotSetObserver(observer));
    }

    public void reset() {
        observer.onChanged(NOT_SET);
    }

    class NotSetObserver<T> implements androidx.lifecycle.Observer<T> {
        androidx.lifecycle.Observer<T> observer;

        public NotSetObserver(androidx.lifecycle.Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged(T t) {
            if (t == NOT_SET) {
                return;
            }
            observer.onChanged(t);
        }
    }


}
