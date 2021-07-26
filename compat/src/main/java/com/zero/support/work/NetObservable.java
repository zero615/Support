package com.zero.support.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class NetObservable extends SerialObservable<Integer> {
    public static final int NETWORK_NONE = -1;
    public static final int NETWORK_MOBILE = 0;
    public static final int NETWORK_WIFI = 1;
    private Context context;
    private boolean init;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
                int netWorkState = getNetWorkState(context);
                if (getValue() != null && getValue() != netWorkState) {
                    setValue(netWorkState);
                }
            }
        }
    };

    public NetObservable(Context context) {
        this.context = context;
        setValue(getNetWorkState(context));
    }

    public static int getNetWorkState(Context context) {
        //得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return NETWORK_NONE;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    @Override
    public synchronized void observe(Observer<Integer> observer, boolean weak) {
        super.observe(observer, weak);
        if (!init) {
            onActive();
        }
    }

    public boolean isMobile() {
        Integer integer = getValue();
        return integer != null && integer == NETWORK_MOBILE;
    }

    protected void onActive() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(receiver, filter);
    }

    protected void onInactive() {
        context.unregisterReceiver(receiver);
    }
}
