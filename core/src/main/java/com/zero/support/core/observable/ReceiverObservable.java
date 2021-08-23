package com.zero.support.core.observable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.zero.support.compat.AppGlobal;
import com.zero.support.core.AppExecutor;

public class ReceiverObservable extends SerialObservable<Intent> {
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
                Bundle bundle = intent.getExtras();
                bundle.size();
                Log.e("xgf", "onReceive: " + bundle + "  " + intent.hashCode());
                boolean test = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, true);
                intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, ConnectivityManager.TYPE_WIFI);

            }
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


    protected void onActive() {
        AppGlobal.getApplication().registerReceiver(receiver, filter);
    }

    protected void onInactive() {
        AppGlobal.getApplication().unregisterReceiver(receiver);
        reset();
    }
}
