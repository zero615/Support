package com.zero.support.work;


public interface Observer<T> {

    void onChanged(T t);
}
