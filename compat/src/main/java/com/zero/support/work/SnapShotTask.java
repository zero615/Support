package com.zero.support.work;


import com.zero.support.util.Observable;

public abstract class SnapShotTask<Param, Result> extends PromiseTask<Param, Result> {
    private final Observable<Snapshot> snapshotObservable = new Observable<>();
    private final Snapshot mSnapshot = new Snapshot();


    public Snapshot getCurrentSnapshot() {
        return mSnapshot;
    }


    public void onSnapshot(Snapshot snapshot) {
        //snapshot
    }

    @Override
    public final void onError(Throwable throwable) {
        super.onError(throwable);
    }


    @Override
    public final void onCancel(Response<Result> resultResponse) {
        super.onCancel(resultResponse);
    }

    @Override
    public void publishProgressChanged(Progress progress) {
        super.publishProgressChanged(progress);
    }

    @Override
    public final void onProgressChanged(Progress progress) {
        super.onProgressChanged(progress);
        mSnapshot.write(progress);
        dispatchSnapshotChange(new Snapshot(mSnapshot));
    }

    @Override
    public void onStatusChanged(int status) {
        super.onStatusChanged(status);
        mSnapshot.write(status);
        if (status >= STATUS_COMPLETED) {
            mSnapshot.write(getResult());
        }
        dispatchSnapshotChange(new Snapshot(mSnapshot));
    }

    public final void dispatchSnapshotChange(Snapshot snapshot) {
        snapshotObservable.setValue(snapshot);
        onSnapshot(snapshot);
    }

    public Observable<Snapshot> snapshot() {
        return snapshotObservable;
    }
}
