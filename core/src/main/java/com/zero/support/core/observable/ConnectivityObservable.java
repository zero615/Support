package com.zero.support.core.observable;

import android.content.Intent;
import android.net.ConnectivityManager;

public class ConnectivityObservable extends ReceiverObservable {

    public ConnectivityObservable() {
        super(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    public ConnectivityObservable(String... actions) {
        super(actions);
    }

    public static boolean isConnected(Intent intent) {
        return intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
    }

    public static int getNetworkType(Intent intent) {
        return intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isWifi(Intent intent) {
        return getNetworkType(intent) == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isMobile(Intent intent) {
        return getNetworkType(intent) == ConnectivityManager.TYPE_MOBILE;
    }


}
