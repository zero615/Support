package com.zero.support.core.observable;

import android.os.ConditionVariable;

public class ObservableFuture<T> implements Observer<T> {
    private final ConditionVariable variable = new ConditionVariable();
    private final Observable<T> observable;
    private volatile T value;

    public ObservableFuture(Observable<T> observable) {
        this.observable = observable;
        value = observable.getValue();
        if (value == null) {
            observable.observe(this);
        }
    }

    public T getValue() {
        if (value != null) {
            return value;
        }
        variable.block();
        return value;
    }

    @Override
    public void onChanged(T t) {
        observable.remove(this);
        value = t;
        variable.open();
    }

    public void reset() {
        if (value != null) {
            variable.close();
            value = null;
            observable.observe(this);
        }
    }
}
