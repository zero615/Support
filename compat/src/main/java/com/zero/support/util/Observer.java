package com.zero.support.util;


public interface Observer<T> {

    void onChanged(T t);
}
