package com.zero.support.work;

import android.os.Parcel;

public class Snapshot {
    //代表任务状态
    private int status;
    private Progress progress = Progress.EMPTY_PROGRESS;
    private Response<?> response;

    public Snapshot() {
    }

    public Snapshot(Snapshot snapshot) {
        if (snapshot != null) {
            this.status = snapshot.status;
            this.progress = snapshot.progress;
            this.response = snapshot.response;
        }
    }


    protected Snapshot(Parcel in) {
        status = in.readInt();
        if (in.readString() != null) {
            progress = Progress.CREATOR.createFromParcel(in);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> Response<T> response() {
        return (Response<T>) response;
    }

    public int status() {
        return status;
    }

    public Progress progress() {
        return progress;
    }

    public void finish(Response<?> response) {
        this.status = Task.STATUS_COMPLETED;
        this.response = response;
    }


    public boolean isEnqueued() {
        return status == Task.STATUS_ENQUEUED;
    }

    public boolean isRunning() {
        return status == Task.STATUS_RUNNING;
    }

    public boolean isFinished() {
        return status == Task.STATUS_COMPLETED;
    }

    public boolean isOK() {
        return response.isSuccessful() && isFinished();
    }

    public Snapshot write(int status) {
        this.status = status;
        return this;
    }

    public Snapshot write(Progress progress) {
        this.progress = progress;
        return this;
    }

    public Snapshot write(Response<?> response) {
        this.response = response;
        return this;
    }
}
