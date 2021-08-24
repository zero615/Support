package com.zero.support.observable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.zero.support.core.AppGlobal;


public class ReceiverLiveEvent extends LiveData<Intent> {
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            postValue(intent);
        }
    };
    private IntentFilter mIntentFilter;

    public ReceiverLiveEvent(String... actions) {
        mIntentFilter = new IntentFilter();
        for (int i = 0; i < actions.length; i++) {
            mIntentFilter.addAction(actions[i]);
        }
    }

    public void observerOnce(final Observer<Intent> observer) {
        observeForever(new Observer<Intent>() {
            @Override
            public void onChanged(Intent intent) {
                removeObserver(this);
                observer.onChanged(intent);
            }
        });
    }

    @Override
    protected void onActive() {
        super.onActive();
        AppGlobal.currentApplication().registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        AppGlobal.currentApplication().unregisterReceiver(mReceiver);
    }
}
