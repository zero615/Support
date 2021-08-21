package com.zero.support.xx.work;

import android.os.Parcel;
import android.os.Parcelable;

public class Progress implements Parcelable, Cloneable {

    public int type;
    public long handled;
    public long total;
    public int stage;
    public int totalStage;
    public long speed;


    public final static Progress EMPTY_PROGRESS = new Progress();

    public Progress() {
        this.handled = 0;
        this.total = 100;
        this.stage = 1;
        this.totalStage = 1;
    }

    public Progress(Progress progress) {
        this.type = progress.type;
        this.handled = progress.handled;
        this.total = progress.total;
        this.stage = progress.stage;
        this.totalStage = progress.totalStage;
        this.speed = progress.speed;
    }

    protected Progress(Parcel in) {
        type = in.readInt();
        handled = in.readLong();
        total = in.readLong();
        stage = in.readInt();
        totalStage = in.readInt();
        speed = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeLong(handled);
        dest.writeLong(total);
        dest.writeInt(stage);
        dest.writeInt(totalStage);
        dest.writeLong(speed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Progress> CREATOR = new Creator<Progress>() {
        @Override
        public Progress createFromParcel(Parcel in) {
            return new Progress(in);
        }

        @Override
        public Progress[] newArray(int size) {
            return new Progress[size];
        }
    };

    public void readFromParcel(Parcel in) {
        type = in.readInt();
        handled = in.readLong();
        total = in.readLong();
        stage = in.readInt();
        totalStage = in.readInt();
        speed = in.readLong();
    }

    @Override
    public String toString() {
        return "ParcelProgress{" +
                "handled=" + handled +
                ", total=" + total +
                ", stage=" + stage +
                ", totalStage=" + totalStage +
                ", speed=" + speed +
                '}';
    }

    public void init(int type, long handled, long total, long speed, int stage, int totalStage) {
        this.type = type;
        this.handled = handled;
        this.total = total;
        this.stage = stage;
        this.totalStage = totalStage;
        this.speed = speed;
    }


    public int progress() {
        if (total <= 0) {
            return 0;
        }
        int progress = (int) (handled * 100L / total);
        return Math.min(progress, 100);
    }

    public void readFromProgress(Progress progress) {
        handled = progress.handled;
        total = progress.total;
        stage = progress.stage;
        totalStage = progress.totalStage;
        speed = progress.speed;
        type = progress.type;
    }

    public void init(long handled) {
        this.handled = handled;
    }

    public void init(long handled, long total) {
        this.handled = handled;
        this.total = total;
    }

    public void init(Progress progress) {
        this.type = progress.type;
        this.handled = progress.handled;
        this.total = progress.total;
        this.stage = progress.stage;
        this.totalStage = progress.totalStage;
        this.speed = progress.speed;
    }

}
