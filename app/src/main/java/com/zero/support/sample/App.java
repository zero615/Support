package com.zero.support.sample;

import android.app.Application;

import com.zero.support.compat.AppGlobal;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppGlobal.initialize(this);
    }
}
