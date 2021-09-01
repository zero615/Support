package com.zero.support.core.observable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.zero.support.core.AppExecutor;
import com.zero.support.core.AppGlobal;

public class ReceiverObservable extends SerialObservable<Intent> {
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setValue(intent);
        }
    };

    private final IntentFilter filter;

    public ReceiverObservable(String... actions) {
        super(AppExecutor.main());
        filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
    }

    public IntentFilter getIntentFilter() {
        return filter;
    }

    protected void onActive() {
        AppGlobal.currentApplication().registerReceiver(receiver, filter);
    }

    protected void onInactive() {
        AppGlobal.currentApplication().unregisterReceiver(receiver);
        reset();
    }
}
