package com.zero.support.glide.app;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

public class PackageModelLoader implements ModelLoader<PackageInfo, Drawable> {

    @Nullable
    @Override
    public LoadData<Drawable> buildLoadData(@NonNull PackageInfo info, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(info.packageName), new PackageDataFetcher(info));
    }


    @Override
    public boolean handles(@NonNull PackageInfo info) {
        return true;
    }
}
