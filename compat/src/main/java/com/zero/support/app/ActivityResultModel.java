package com.zero.support.app;


import android.content.Intent;

import com.zero.support.util.Observable;


public class ActivityResultModel extends ActivityModel {
    private final Observable<ActivityResultEvent> observable = new Observable<>();
    private volatile Intent data;
    private volatile int resultCode;
    private Intent intent;
    private boolean executed;

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public boolean isExecuted() {
        return executed;
    }

    public ActivityResultModel(Intent intent) {
        this.intent = intent;
    }

    void dispatchResult(int resultCode, Intent data) {
        this.data = data;
        this.resultCode = resultCode;
        this.executed = false;
        observable.setValue(new ActivityResultEvent(this, resultCode, data));
    }

    public Observable<ActivityResultEvent> result() {
        return observable;
    }

    public Intent intent() {
        return intent;
    }

    @Override
    public String toString() {
        return "ActivityResultModel{" +
                "data=" + data +
                ", resultCode=" + resultCode +
                ", observable=" + observable +
                ", intent=" + intent +
                '}';
    }
}
