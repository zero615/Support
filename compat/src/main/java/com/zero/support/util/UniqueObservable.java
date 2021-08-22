package com.zero.support.util;

import com.zero.support.util.Observer;

import java.util.Objects;

public class UniqueObservable<T> extends Observable<T> {
    public UniqueObservable() {
    }

    @Override
    protected void performDispatch(Observer<T> observer, Object value, int version) {
        if (observer != null || !Objects.equals(value, getValue())) {
            super.performDispatch(observer, value, version);
        }
    }
}
