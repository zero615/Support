package com.zero.support.xx.version;

import android.app.ActivityThread;
import android.content.Context;
import android.os.Build;

import com.excean.support.download.FileDownloadTask;
import com.excean.support.download.FileRequest;
import com.excean.support.util.Singleton;
import com.excean.support.work.TaskManager;

import java.io.File;


public class DownloadManger {
    private static Singleton<DownloadManger> singleton = new Singleton<DownloadManger>() {
        @Override
        protected DownloadManger create() {
            return new DownloadManger(ActivityThread.currentApplication());
        }
    };

    public static DownloadManger getDefault() {
        return singleton.get();
    }

    private Context context;

    private final TaskManager<FileRequest, FileDownloadTask> queue = new TaskManager<>(FileDownloadTask.class);

    private final File root;

    TaskManager<FileRequest, FileDownloadTask> getQueue() {
        return queue;
    }

    public DownloadManger(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT > 24) {
            this.root = new File(context.getFilesDir(), "download");
        } else {
            this.root = context.getExternalFilesDir("download");
        }
    }


    public File getRoot() {
        return root;
    }

    public File getDownloadFile(String name) {
        return new File(root, name);
    }

    public boolean isDownloading(FileRequest request) {
        FileDownloadTask task = queue.get(request);
        if (task == null || task.isFinished()) {
            return false;
        }
        return true;
    }
}
