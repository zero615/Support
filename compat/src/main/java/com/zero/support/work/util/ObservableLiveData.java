package com.zero.support.work.util;


import androidx.lifecycle.MutableLiveData;

import com.zero.support.work.AppExecutor;
import com.zero.support.work.Observable;
import com.zero.support.work.Observer;


@SuppressWarnings("ALL")
public class ObservableLiveData<T> extends MutableLiveData<T> {
    private final Observable<T> observable;
    private final Observer<T> observer = new Observer<T>() {
        @Override
        public void onChanged(T t) {
            if (AppExecutor.isMainThread()) {
                setValue(t);
            } else {
                postValue(t);
            }
        }
    };
    private final boolean weak;

    public ObservableLiveData(Observable<T> observable, boolean weak) {
        this.observable = observable;
        this.weak = weak;
        this.observable.observe(observer, weak);
    }
}
