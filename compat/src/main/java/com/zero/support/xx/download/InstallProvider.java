package com.zero.support.xx.download;


import android.app.ActivityThread;
import android.app.Application;
import android.net.Uri;

import com.excean.support.compat.FileProvider;
import com.excean.support.version.DownloadManger;

import java.io.File;

public class InstallProvider extends FileProvider {
    private static String authority;

    static {
        Application app = ActivityThread.currentApplication();
        authority = app.getPackageName() + ":file.provider";
        FileProvider.SimplePathStrategy strategy = new FileProvider.SimplePathStrategy(authority);
        strategy.addRoot("upgrade", DownloadManger.getDefault().getRoot());
        FileProvider.registerPathStrategy(authority, strategy);
    }

    public static Uri getUriForFile(File file) {
        return getUriForFile(ActivityThread.currentApplication(), authority, file);
    }
}
