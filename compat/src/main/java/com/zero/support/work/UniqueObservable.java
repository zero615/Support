package com.zero.support.work;

import java.util.Objects;
import java.util.concurrent.Executor;

public class UniqueObservable<T> extends SerialObservable<T> {
    public UniqueObservable() {
    }

    public UniqueObservable(Executor executor) {
        super(executor);
    }

    @Override
    protected void onPostPerformDispatch(Observer<T> observer, Object value, int version) {
        if (observer!=null||!Objects.equals(value, getValue())) {
            super.onPostPerformDispatch(observer, value, version);
        }
    }
}
