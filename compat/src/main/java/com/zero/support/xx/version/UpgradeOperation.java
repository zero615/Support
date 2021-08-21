package com.zero.support.xx.version;

import android.app.Activity;
import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;

import com.excean.support.download.FileDownloadTask;
import com.excean.support.work.NetObservable;
import com.excean.support.work.Observable;
import com.excean.support.work.Observer;
import com.excean.support.work.Response;
import com.excean.support.work.Snapshot;

public class UpgradeOperation<T> {

    private final Observable<Snapshot> snapshot = new Observable<>();
    private DownloadManger manger;
    private volatile T versionInfo;
    private final Observable<Response<T>> version = new Observable<>();

    private final VersionWatcher versionWatcher = new VersionWatcher();
    private final UpgradeProgress upgradeProgress = new UpgradeProgress();
    private final VersionObserver versionObserver = new VersionObserver();

    private boolean allowCellData;
    private volatile boolean fetching;


    private volatile VersionConverter<T> converter;
    private final Observable<VersionState<T>> versionState = new Observable<>();
    private NetObservable netObserve;
    private Context context;

    public UpgradeOperation(VersionConverter<T> converter) {
        this.manger = DownloadManger.getDefault();
        this.converter = converter;
        this.context = ActivityThread.currentApplication();
        this.netObserve = new NetObservable(ActivityThread.currentApplication());
    }

    public Observable<Response<T>> checkUpdate() {
        if (fetching || versionInfo != null) {
            return version;
        }
        fetching = true;
        getVersionInfoInBackground(versionObserver);
        return version;
    }


    public void enableCellData(boolean allowCellData) {
        this.allowCellData = allowCellData;
    }

    public boolean isAllowCellData() {
        return allowCellData;
    }


    public Observable<Snapshot> snapshot() {
        return snapshot;
    }

    public Observable<VersionState<T>> versionState() {
        return versionState;
    }


    public void requestUpgrade(Activity activity, boolean allowCellData) {
        this.allowCellData = allowCellData;
        requestContinued(activity);
    }

    public void requestContinued() {
        manger.getQueue().opt(converter.createFileRequest(versionInfo, allowCellData)).snapshot().observe(upgradeProgress);
    }

    public void requestContinued(Activity activity) {
//        activity.startService(new Intent(activity, DownloadService.class));
        requestContinued();
    }


    public void requestStopUpgrade() {
        FileDownloadTask task = manger.getQueue().get(converter.createFileRequest(versionInfo, allowCellData));
        if (task != null) {
            task.cancel(true);
        }
    }

    public NetObservable getNetObserve() {
        return netObserve;
    }

    public Observable<Response<T>> version() {
        return version;
    }


    private void autoCheckUpdate() {
        netObserve.observe(versionWatcher);
    }

    public void clearVersion() {
        fetching = false;
        versionInfo = null;
        version.reset();
        versionState.reset();
    }


    private class VersionWatcher implements Observer<Integer> {

        @Override
        public void onChanged(Integer integer) {
            integer = NetObservable.getNetWorkState(context);
            if (integer != NetObservable.NETWORK_NONE) {
                netObserve.remove(this);
                checkUpdate();
            }
        }
    }

    private class VersionObserver implements Observer<Response<T>> {

        @Override
        public void onChanged(Response<T> response) {
            fetching = false;
            if (!response.isSuccessful()) {
                autoCheckUpdate();
            } else {
                if (response.data() == null) {
                    response = Response.success(null);
                }
                versionInfo = response.data();
                version.setValue(response);
                versionState.setValue(new VersionState<>(VersionState.STATE_PENDING, versionInfo));
            }

        }
    }

    private class UpgradeProgress implements Observer<Snapshot> {
        private int type = -1;
        private Application app = ActivityThread.currentApplication();

        @Override
        public void onChanged(Snapshot snapshot) {
            if (snapshot.isEnqueued()) {
                versionState.setValue(new VersionState<>(VersionState.STATE_DOWNLOADING, versionInfo));
            } else if (snapshot.isFinished()) {
                versionState.setValue(new VersionState<>(VersionState.STATE_FINISH, versionInfo));
            } else if (snapshot.isRunning()) {
                int t = snapshot.progress().type;
                if (t != type) {
                    type = snapshot.progress().type;
                }
            }
            UpgradeOperation.this.snapshot.setValue(snapshot);
        }
    }

    public T getVersionInfo() {
        return versionInfo;
    }

    public Response<T> fetchVersionInfo() {
        if (versionInfo != null) {
            return Response.success(versionInfo);
        }
        return converter.fetchVersion().getFuture().getValue();
    }


    public void getVersionInfoInBackground(final Observer<Response<T>> observer) {
        if (versionInfo != null) {
            observer.onChanged(Response.success(versionInfo));
            return;
        }
        converter.fetchVersion().asLive().observeForever(new androidx.lifecycle.Observer<Response<T>>() {
            @Override
            public void onChanged(Response<T> response) {
                observer.onChanged(response);
            }
        });

    }


}
