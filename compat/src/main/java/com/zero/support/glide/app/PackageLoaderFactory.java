package com.zero.support.glide.app;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class PackageLoaderFactory implements ModelLoaderFactory<PackageInfo, Drawable> {


  @NonNull
  @Override
  public ModelLoader<PackageInfo, Drawable> build(@NonNull MultiModelLoaderFactory multiFactory) {
    return new PackageModelLoader();
  }

  @Override
  public void teardown() { 
    // Do nothing.
  }
}