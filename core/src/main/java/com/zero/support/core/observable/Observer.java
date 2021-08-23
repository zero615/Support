package com.zero.support.core.observable;


public interface Observer<T> {

    void onChanged(T t);
}
