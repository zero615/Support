package com.zero.support.core.observable;

import android.content.Intent;
import android.text.TextUtils;

public class PackageObservable extends ReceiverObservable {
    public PackageObservable() {
        super(Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REPLACED);
    }

    public PackageObservable(String... actions) {
        super(actions);
    }

    public static boolean isRemoved(Intent intent) {
        return TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED) && intent.getBooleanExtra(Intent.EXTRA_DATA_REMOVED, false);
    }

    public static boolean isReplaced(Intent intent) {
        return TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED) && intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
    }

    public static boolean isAdded(Intent intent) {
        return TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED) && !intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
    }
}
