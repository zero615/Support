package com.zero.support.glide.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.zero.support.core.AppGlobal;


public class PackageDataFetcher implements DataFetcher<Drawable> {
    private PackageInfo model;

    public PackageDataFetcher(PackageInfo model) {
        this.model = model;
    }


    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Drawable> callback) {
        PackageManager manager = AppGlobal.currentApplication().getPackageManager();
        callback.onDataReady(manager.getApplicationIcon(model.applicationInfo));
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<Drawable> getDataClass() {
        return Drawable.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
