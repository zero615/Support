package com.zero.support.xx.version;

public class VersionState<T> {
    public static final int STATE_PENDING = 0;
    public static final int STATE_DOWNLOADING = 1;
    public static final int STATE_FINISH = 2;
    private int state;
    private T versionInfo;


    public VersionState(int state, T versionInfo) {
        this.state = state;
        this.versionInfo = versionInfo;
    }

    @Override
    public String toString() {
        return "VersionState{" +
                "state=" + state +
                ", versionInfo=" + versionInfo +
                '}';
    }

    public int getState() {
        return state;
    }

    public T getVersionInfo() {
        return versionInfo;
    }

    public boolean isPending() {
        return state == STATE_PENDING&&versionInfo!=null;
    }

    public boolean isDownloading() {
        return state == STATE_DOWNLOADING;
    }

    public boolean isFinish() {
        return state == STATE_FINISH;
    }
}
