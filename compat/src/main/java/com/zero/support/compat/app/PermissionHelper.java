package com.zero.support.compat.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class PermissionHelper {

    private static final String TAG = "EasyPermissions";

    public static boolean hasPermissions(@NonNull Context context, @Size(min = 1) @NonNull String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");

            // DANGER ZONE!!! Changing this will break the library.
            return true;
        }

        // Null context may be passed if we have detected Low API (less than M) so getting
        // to this point with a null context should not be possible.
        if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasPermissionPermanentlyDenied(@NonNull Activity host, @NonNull List<String> deniedPermissions) {
        for (String permission : deniedPermissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(host, permission)) {
                return true;
            }
        }
        return false;
    }

    public static void requestPermission(RequestViewModel viewModel, int requestCode, String... perms) {
        if (hasPermissions(viewModel.requireActivity(), perms)) {
            notifyAlreadyHasPermissions(viewModel, perms);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (viewModel.getFragment() != null) {
                    viewModel.requireFragment().requestPermissions(perms, requestCode);
                } else {
                    viewModel.requireActivity().requestPermissions(perms, requestCode);
                }
            }
        }
    }

    private static void notifyAlreadyHasPermissions(@NonNull RequestViewModel viewModel, @NonNull String[] perms) {
        int[] grantResults = new int[perms.length];
        for (int i = 0; i < perms.length; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        }
        viewModel.dispatchRequestPermission(perms, grantResults);
    }
}
